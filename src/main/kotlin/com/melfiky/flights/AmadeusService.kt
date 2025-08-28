package com.melfiky.flights

import com.melfiky.flights.FlightsController.TripRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class AmadeusService(
    private val amadeusApi: AmadeusApi,
    private val airportRepository: AirportRepository,
    private val airlineRepository: AirlineRepository,
    private val aircraftRepository: AircraftRepository,
) {

    private val logger = LoggerFactory.getLogger(AmadeusService::class.java)
    private val airports: Map<String, Airport> = airportRepository.findAll().associateBy { it.iata }
    private val airlines: Map<String, Airline> = airlineRepository.findAll().associateBy { it.code }
    private val aircraft: Map<String, Aircraft> = aircraftRepository.findAll().associateBy { it.iata }

    @Cacheable("amadeus")
    fun getFlights(
        origin: String,
        destination: String,
        departureDate: String,
        returnDate: String?,
        nonStop: Boolean = true,
    ): FlightOffersSearchResponse? {
        return amadeusApi.getFlights(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            returnDate = returnDate,
            nonStop = nonStop,
            adults = 1
        )
    }

    @Cacheable("amadeus")
    fun multiCityFlightOffers(request: TripRequest): FlightResponse {
        val offersRequest = MultiCityRequest(
            originDestinations = request.legs.map { leg ->
                OriginDestination(
                    id = leg.id.toString(),
                    origin = leg.origin,
                    destination = leg.destination,
                    departureDate = DateTimeRange(
                        date = leg.departureDate
                    )
                )
            }
        )

        val response = amadeusApi.multiCityFlightOffers(offersRequest)
        return response.toFlightResponse(airports, airlines, aircraft)
    }

}

private fun AmadeusSegment.toSegment(
    airports: Map<String, Airport>,
    airlines: Map<String, Airline>,
    aircraft: Map<String, Aircraft>
): Segment? {
    val departureAirport = airports[departure.iataCode] ?: return null
    val arrivalAirport = airports[arrival.iataCode] ?: return null
    val carrier = airlines[carrierCode] ?: return null
    val operating = operating?.carrierCode.let { airlines[it] }
    val aircraft = aircraft[this.aircraft.code] ?: return null

    return Segment(
        id = id.toInt(),
        departureAirport = departureAirport,
        departureAt = LocalDateTime.parse(departure.at),
        arrivalAirport = arrivalAirport,
        arrivalAt = LocalDateTime.parse(arrival.at),
        carrier = carrier,
        operating = operating,
        aircraft = aircraft,
        number = number,
        duration = Duration.parse(duration),
        numberOfStops = numberOfStops,
    )
}

private fun AmadeusItinerary.toItinerary(
    airports: Map<String, Airport>,
    airlines: Map<String, Airline>,
    aircraft: Map<String, Aircraft>
): Itinerary? {
    val segments = segments.map { it.toSegment(airports, airlines, aircraft) }
    if (segments.any { it == null }) return null
    val carriers = segments.mapNotNull { it?.carrier }.toSet()
    val carriersText = carriers.joinToString { it.name }
    val carrierLogo = if (carriers.size == 1) carriers.first().logo else null
    val first = segments.first() ?: return null
    val last = segments.last() ?: return null
    val departureTime = first.departureAt.toString().substring(11, 16)
    val arrivalTime = last.arrivalAt.toString().substring(11, 16)
    val noOfStops = segments.size - 1

    return Itinerary(
        segments = segments.filterNotNull(),
        carriers = carriersText,
        carrierLogo = carrierLogo,
        from = first.departureAirport.iata.uppercase(),
        to = last.arrivalAirport.iata.uppercase(),
        departureTime = departureTime,
        arrivalTime = arrivalTime,
        duration = Duration.parse(duration),
        stops = noOfStops,
        isDirect = noOfStops == 0
    )
}

private fun FlightOffer.toFlight(
    airports: Map<String, Airport>,
    airlines: Map<String, Airline>,
    aircraft: Map<String, Aircraft>
): Flight? {
    val itineraries = itineraries.map { it.toItinerary(airports, airlines, aircraft) }
    if (itineraries.any { it == null }) return null

    val filtered = itineraries.filterNotNull()
    return Flight(
        id = this.id.toInt(),
        itineraries = filtered,
        isDirect = filtered.all { it.isDirect },
        totalStops = filtered.sumOf { it.stops },
        totalDuration = filtered.map { it.duration }.reduce { acc, duration -> acc + duration },
    )
}

private fun FlightOffersSearchResponse.toFlightResponse(
    airports: Map<String, Airport>,
    airlines: Map<String, Airline>,
    aircraft: Map<String, Aircraft>
): FlightResponse {
    val flights = this.data.mapNotNull { it.toFlight(airports, airlines, aircraft) }
        .sortedWith(
            compareByDescending<Flight> { it.isDirect }
                .thenBy { it.totalDuration }
                .thenBy { it.totalStops }
        )

    return FlightResponse(flights = flights)
}
