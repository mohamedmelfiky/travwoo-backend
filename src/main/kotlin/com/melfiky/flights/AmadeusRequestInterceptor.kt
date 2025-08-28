package com.melfiky.flights

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class AmadeusRequestInterceptor(
    private val amadeusProperties: AmadeusProperties,
) : ClientHttpRequestInterceptor {

    private var accessToken: String? = null
    private val restClient = RestClient.builder().build()

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        // Skip authentication for token endpoint
        if (request.uri.path.contains("/v1/security/oauth2/token")) {
            return execution.execute(request, body)
        }

        // Get or refresh token
        val token = getAccessToken()

        // Add Authorization header
        request.headers.set(HttpHeaders.AUTHORIZATION, "Bearer $token")

        // Execute the request
        val response = execution.execute(request, body)

        // If unauthorized, refresh token and retry
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            // Clear the current token
            accessToken = null

            // Get a new token
            val newToken = getAccessToken()

            // Create a new request with the new token
            request.headers.set(HttpHeaders.AUTHORIZATION, "Bearer $newToken")

            // Retry the request
            return execution.execute(request, body)
        }

        return response
    }

    private fun getAccessToken(): String {
        // Return existing token if available
        accessToken?.let { return it }

        // Prepare request for token
        val tokenUrl = "https://test.api.amadeus.com/v1/security/oauth2/token"

        val params = LinkedMultiValueMap<String, String>()
        params.add("client_id", amadeusProperties.clientId)
        params.add("client_secret", amadeusProperties.clientSecret)
        params.add("grant_type", amadeusProperties.grantType)

        // Make request to get token
        val response = restClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(Map::class.java)

        // Extract and store token
        accessToken = response?.get("access_token") as String
        return accessToken!!
    }
}
