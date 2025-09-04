package com.melfiky.flights.payment

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

interface PaymobClient {

    @PostExchange("v1/intention/")
    fun intention(@RequestBody request: PaymobIntentionRequest): PaymobIntentionResponse

}