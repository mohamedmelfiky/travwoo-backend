package com.melfiky.flights

import com.melfiky.flights.printout.PrintOutService
import com.melfiky.flights.printout.PrintoutRequest
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.util.UUID


@RestController
class FlightsController(
    private val amadeusService: AmadeusService,
    private val printOutService: PrintOutService,
    private val airportRepository: AirportRepository,
    private val airlineRepository: AirlineRepository,
    private val aircraftRepository: AircraftRepository
) {

    private val logger = LoggerFactory.getLogger(AmadeusService::class.java)

    @GetMapping("v2/shopping/flight-offers")
    fun getFlights(
        @RequestParam("originLocationCode") origin: String,
        @RequestParam("destinationLocationCode") destination: String,
        @RequestParam("departureDate") departureDate: String,
        @RequestParam("returnDate", required = false) returnDate: String? = null,
    ): FlightOffersSearchResponse? {
        logger.info("Retrieving flights for $origin")
        return amadeusService.getFlights(
            origin,
            destination,
            departureDate,
            returnDate
        )
    }

    data class TripRequest(
        val legs: List<Leg>
    )

    data class Leg(
        val id: Int,
        val origin: String,
        val destination: String,
        val departureDate: String,
    )

    @PostMapping("v2/shopping/flight-offers")
    fun findTrips(@RequestBody request: TripRequest): FlightResponse {
        logger.info("Multicity FlightOffers : $request")
        return amadeusService.multiCityFlightOffers(request)
    }

}