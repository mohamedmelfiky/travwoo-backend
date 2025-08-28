package com.melfiky.flights

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class AircraftDataLoader(
    private val aircraftRepository: AircraftRepository,
    private val mapper: ObjectMapper
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(AircraftDataLoader::class.java)

    override fun run(vararg args: String?) {
        if (aircraftRepository.count() == 0L) {
            logger.info("Loading aircrafts data from JSON...")
            try {
                val inputStream = ClassPathResource("aircrafts.json").inputStream
                val aircrafts: List<Aircraft> = mapper.readValue(
                    inputStream,
                    object : TypeReference<List<Aircraft>>() {}
                )

                aircraftRepository.insertAll(aircrafts)
                logger.info("Successfully loaded ${aircrafts.size} aircrafts.")
            } catch (e: Exception) {
                logger.error("Error loading aircrafts data: ${e.message}")
                e.printStackTrace()
            }
        } else {
            logger.info("Aircrafts data already exists in DB. Skipping loading.")
        }
    }
}