package com.melfiky.flights

import org.springframework.data.repository.ListCrudRepository

interface AirportRepository : ListCrudRepository<Airport, String>, WithInsert<Airport> {

    fun findAllByIataIn(codes: List<String>): List<Airport>

}