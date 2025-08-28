package com.melfiky.flights

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.nio.charset.StandardCharsets


@Configuration
class AmadeusConfig {

    private val logger = LoggerFactory.getLogger(AmadeusConfig::class.java)

    @Bean
    fun amadeusApi(
        amadeusRequestInterceptor: AmadeusRequestInterceptor
    ): AmadeusApi {
        val client = RestClient.builder()
            .baseUrl("https://test.api.amadeus.com/")
            .requestInterceptor(amadeusRequestInterceptor)
            .requestInterceptor { request, body, execution ->
                logRequest(request, body)
                val response = execution.execute(request, body)
                logResponse(request, response)
                response
            }
            .build()

        val adapter = RestClientAdapter.create(client)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(AmadeusApi::class.java)
    }

    private fun logRequest(request: HttpRequest, body: ByteArray?) {
        logger.info("Request: {} {}", request.method, request.uri)
        if (body != null && body.isNotEmpty()) {
            logger.info("Request body: {}", String(body, StandardCharsets.UTF_8))
        }
    }

    private fun logResponse(request: HttpRequest?, response: ClientHttpResponse) {
        logger.info("Response status: {}", response.statusCode)
//        val responseBody: ByteArray = response.body.readAllBytes()
//        if (responseBody.isNotEmpty()) {
//            logger.info("Response body: {}", String(responseBody, StandardCharsets.UTF_8))
//        }
    }

}
