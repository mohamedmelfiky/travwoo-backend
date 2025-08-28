package com.melfiky.flights

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "amadeus")
data class AmadeusProperties(
    val clientId: String,
    val clientSecret: String,
    val grantType: String,
)
