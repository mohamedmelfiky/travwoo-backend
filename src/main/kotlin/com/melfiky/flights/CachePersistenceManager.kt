package com.melfiky.flights

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.notExists
import kotlin.io.path.readBytes

@Component
class CachePersistenceManager(
    val cacheManager: CacheManager,
    val mapper: ObjectMapper,
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(CachePersistenceManager::class.java)

    override fun run(vararg args: String?) {
        loadCacheOnStartup()
    }

    fun loadCacheOnStartup() {
        val filePath = Paths.get("amadeus.json")
        if (filePath.notExists()) return

        filePath.readBytes()

        // Read the data as a Map with String keys
        val rawData = mapper.readValue<Map<String, Any>?>(filePath.toFile())

        val cache = cacheManager.getCache("amadeus") ?: return
        val caffeineCache = (cache as CaffeineCache).nativeCache

        // Convert string keys to SimpleKey objects and put in cache
        rawData?.forEach { (keyStr, value) ->
            // Parse SimpleKey from string representation
//            if (keyStr.startsWith("SimpleKey [") && keyStr.endsWith("]")) {
//                val paramsStr = keyStr.removePrefix("SimpleKey [").removeSuffix("]")
//                val params = parseSimpleKeyParams(paramsStr)
//                val simpleKey = SimpleKey(*params.toTypedArray())
//                // Convert LinkedHashMap to FlightOffersSearchResponse if needed
//                val convertedValue = convertValueIfNeeded(value)
//                caffeineCache.put(simpleKey, convertedValue)
//            } else {
                // If not a SimpleKey format, just use the string key
                // Convert LinkedHashMap to FlightOffersSearchResponse if needed
                val convertedValue = convertValueIfNeeded(value)
                val tripRequest = mapper.readValue<TripRequest>(keyStr)
                val response = mapper.convertValue<FlightResponse>(value)
                caffeineCache.put(tripRequest, response)
//            }
        }

        logger.info("Loaded cache ${caffeineCache.estimatedSize()}")
        logger.info("Keys : ${caffeineCache.asMap().keys}")
        logger.info("Keys hashcode : ${caffeineCache.asMap().keys.map { it.hashCode() }}")
//        amadeusRunner.run()
    }

    // Helper method to parse SimpleKey parameters from string
    private fun parseSimpleKeyParams(paramsStr: String): List<Any?> {
        val result = mutableListOf<Any?>()
        val parts = splitParams(paramsStr)

        for (part in parts) {
            val trimmed = part.trim()
            when {
                trimmed == "null" -> result.add(null)
                trimmed == "true" -> result.add(true)
                trimmed == "false" -> result.add(false)
                trimmed.toIntOrNull() != null -> result.add(trimmed.toInt())
                trimmed.toLongOrNull() != null -> result.add(trimmed.toLong())
                trimmed.toDoubleOrNull() != null -> result.add(trimmed.toDouble())
                else -> result.add(trimmed)
            }
        }

        return result
    }

    // Helper method to split parameters, handling commas inside quotes
    private fun splitParams(paramsStr: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (char in paramsStr) {
            when {
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }

                char == '"' -> inQuotes = !inQuotes
                else -> current.append(char)
            }
        }

        // Add the last part
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }

        return result
    }

    // Helper method to convert LinkedHashMap to FlightOffersSearchResponse if needed
    private fun convertValueIfNeeded(value: Any?): Any? {
        if (value is Map<*, *>) {
            // Check if this map looks like a FlightOffersSearchResponse
            if (value.containsKey("meta") && value.containsKey("data")) {
                // Convert the map to a FlightOffersSearchResponse using Jackson
                return mapper.convertValue(value, FlightOffersSearchResponse::class.java)
            }
        }
        return value
    }

    @EventListener(ContextClosedEvent::class)
    fun saveCacheOnShutdown() {
        val cache = cacheManager.getCache("amadeus") ?: return
        val caffeineCache = (cache as CaffeineCache).nativeCache
//        val data = caffeineCache.asMap()
        val data = caffeineCache.asMap().mapKeys { entry -> mapper.writeValueAsString(entry.key) }
        logger.info("Keys hashcode : ${data.keys.map { it.hashCode() }}")
        logger.info("Saving data")
        val filePath = Paths.get("amadeus.json")
        val writer = Files.newBufferedWriter(filePath)
        logger.info("Writing data : ${mapper.writeValueAsString(data)}")
        writer.use { writer ->
            mapper.writeValue(writer, data)
        }
    }

}
