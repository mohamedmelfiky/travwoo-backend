package com.melfiky.flights.payment

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymobIntentionResponse(
    @param:JsonProperty(value = "client_secret")
    val clientSecret: String,
)