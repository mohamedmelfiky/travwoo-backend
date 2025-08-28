package com.melfiky.flights

import org.springframework.data.repository.ListCrudRepository

interface AirlineRepository : ListCrudRepository<Airline, String>, WithInsert<Airline> {
}