package com.melfiky.flights

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("AIRLINES")
data class Airline(
    @Id val code: String,
    val name: String,
    val logo: String,
)
