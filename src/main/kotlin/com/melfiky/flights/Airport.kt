package com.melfiky.flights

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("AIRPORTS")
data class Airport(
    @Id val icao: String,
    val iata: String,
    val name: String,
    val city: String,
    val state: String,
    val countryCode: String,
    val country: String,
    val tz: String
)