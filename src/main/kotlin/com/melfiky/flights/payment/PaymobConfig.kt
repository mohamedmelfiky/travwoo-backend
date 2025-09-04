package com.melfiky.flights.payment

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class PaymobConfig {

    @Bean
    fun paymobClient(): PaymobClient {
        val client = RestClient.builder()
            .baseUrl("https://accept.paymob.com/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(
                HttpHeaders.AUTHORIZATION,
                "Token egy_sk_test_f1c05b94282a6958d03da8eba86d9b71639dbaf173e889c59430bd8f6d28820a"
            )
            .build()

        val adapter = RestClientAdapter.create(client)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(PaymobClient::class.java)
    }

}