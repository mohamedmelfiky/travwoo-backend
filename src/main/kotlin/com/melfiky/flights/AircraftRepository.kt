package com.melfiky.flights

import org.springframework.data.repository.ListCrudRepository

interface AircraftRepository : ListCrudRepository<Aircraft, String>, WithInsert<Aircraft> {
}