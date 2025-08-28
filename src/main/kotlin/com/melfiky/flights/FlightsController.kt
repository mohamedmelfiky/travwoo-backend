package com.melfiky.flights

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

    @PostMapping("printout")
    fun printOut(@RequestBody request: PrintoutRequest): ResponseEntity<InputStreamResource> {
        val response = amadeusService.multiCityFlightOffers(TripRequest(request.legs))

//        val response = amadeusService.getFlights(
//            request.origin,
//            request.destination,
//            request.departureDate,
//            request.returnDate,
//        )

        val offer = response.flights.find { it.id == request.offerId.toInt() } ?: return ResponseEntity<InputStreamResource>(
            HttpStatus.BAD_REQUEST
        )
        logger.info("Processing $offer")
//
//        val iataCodes = offer.itineraries.map { itinerary ->
//            itinerary.segments.map { listOf(it.departure.iataCode, it.arrival.iataCode) }.flatten()
//        }.flatten()

//        val airports = airportRepository.findAllByIataIn(iataCodes).associateBy { it.iata }

//        logger.info("Processing $airports")

//        val airlinesCode = offer.itineraries.map { itinerary ->
//            itinerary.segments.map { it.carrierCode }
//        }.flatten()

//        val airlines = airlineRepository.findAllById(airlinesCode)
//            .filterNotNull()
//            .associateBy { it.code }

//        logger.info("Processing $airlines")
//
//        val aircraftCodes = offer.itineraries.map { itinerary ->
//            itinerary.segments.map { it.aircraft.code }
//        }.flatten()

//        val aircrafts = aircraftRepository.findAllById(aircraftCodes)
//            .filterNotNull()
//            .associateBy { it.iata }

//        logger.info("Processing $aircrafts")
//
        val bytes = printOutService.htmlToPdf(
            request.passengers.map { "${it.lastName} / ${it.firstName} ${it.title}" },
            offers = offer.toFlightRepresentation()
        )

        val headers = HttpHeaders()
        val uuid = UUID.randomUUID()
        headers.contentDisposition = ContentDisposition
            .inline()
            .filename("$uuid.pdf")
            .build()

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(InputStreamResource(ByteArrayInputStream(bytes)))
//            .body(InputStreamResource(ByteArrayInputStream("".toByteArray())))
    }

}