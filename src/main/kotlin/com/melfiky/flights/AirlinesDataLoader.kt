package com.melfiky.flights

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class AirlinesDataLoader(
    private val airlineRepository: AirlineRepository,
    private val mapper: ObjectMapper
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(AirlinesDataLoader::class.java)

    private data class AirlineCsv(
        val id: String,
        val name: String,
        val logo: String,
    )

    override fun run(vararg args: String?) {
        if (airlineRepository.count() == 0L) {
            logger.info("Loading airlines data from JSON...")
            try {
                val inputStream = ClassPathResource("airlines.json").inputStream
                val airlinesCsv = mapper.readValue(
                    inputStream,
                    object : TypeReference<List<AirlineCsv>>() {}
                )

                val airlines = airlinesCsv.map { Airline(it.id, it.name, it.logo) }

                airlineRepository.insertAll(airlines)
                logger.info("Successfully loaded ${airlines.size} airlines.")
            } catch (e: Exception) {
                logger.error("Error loading airlines data: ${e.message}")
                e.printStackTrace()
            }
        } else {
            logger.info("Airlines data already exists in DB. Skipping loading.")
        }
    }
}