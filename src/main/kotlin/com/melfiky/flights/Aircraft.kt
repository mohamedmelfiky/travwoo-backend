package com.melfiky.flights

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("AIRCRAFTS")
data class Aircraft(
    @Id val iata: String,
    val name: String,
)
