package com.melfiky.flights

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class AirportDataLoader(
    private val airportRepository: AirportRepository,
    private val mapper: ObjectMapper
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(AirportDataLoader::class.java)

    override fun run(vararg args: String?) {
        if (airportRepository.count() == 0L) {
            logger.info("Loading airport data from JSON...")
            try {
                val inputStream = ClassPathResource("airports.json").inputStream
                val airports: List<Airport> = mapper.readValue(
                    inputStream,
                    object : TypeReference<List<Airport>>() {}
                )

                airportRepository.insertAll(airports)
                logger.info("Successfully loaded ${airports.size} airports.")
            } catch (e: Exception) {
                logger.error("Error loading airport data: ${e.message}")
                e.printStackTrace()
            }
        } else {
            logger.info("Airport data already exists in DB. Skipping loading.")
        }
    }
}