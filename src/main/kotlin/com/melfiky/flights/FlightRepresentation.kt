package com.melfiky.flights

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

data class FlightRepresentation(
    val route: String,
    val status: String,
    val departureDate: String,
    val departureTime: String,
    val departureAirport: String,
    val arrivalDate: String,
    val arrivalTime: String,
    val arrivalAirport: String,
    val checkInBy: String,
    val airlineCode: String,
    val airlineName: String,
    val number: String,
    val fareType: String,
    val baggage: String,
    val duration: String,
    val aircraft: String,
    val onBoard: String,
)

// Thursday, 17 April, 2025
val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm")

fun Flight.toFlightRepresentation(): List<FlightRepresentation> {
    return itineraries.map { itinerary ->
        itinerary.segments.map { segment ->
            val departureAirportName = segment.departureAirport.name
            val departureCity = segment.departureAirport.city
            val departureDate = segment.departureAt.format(dateFormatter)
            val departureTime = segment.departureAt.format(timeFormatter)

            val checkInDateTime = segment.departureAt.minusHours(3)
            val checkInDate = checkInDateTime.format(dateFormatter)
            val checkInTime = checkInDateTime.format(timeFormatter)

            val arrivalAirportName = segment.arrivalAirport.name
            val arrivalCity = segment.arrivalAirport.city
            val arrivalDate = segment.arrivalAt.format(dateFormatter)
            val arrivalTime = segment.arrivalAt.format(timeFormatter)

            val airlineCode = segment.carrier.code
            val airlineName = segment.carrier.name
            val flightNumber = segment.number

//            val duration = Duration.parseIsoString(segment.duration)
//            val aircraftCode = segment.aircraft.code
//            val aircraft = aircrafts[aircraftCode]
            val aircraftName = segment.aircraft.name

            val route = "${segment.departureAirport.city} - ${segment.arrivalAirport.city}"
            FlightRepresentation(
                route = route,
                status = "CONFIRMED",
                departureDate = departureDate,
                departureTime = departureTime,
                departureAirport = "$departureCity, $departureAirportName",
                arrivalDate = arrivalDate,
                arrivalTime = arrivalTime,
                arrivalAirport = "$arrivalCity, $arrivalAirportName",
                checkInBy = "$checkInTime ($checkInDate)",
                airlineCode = airlineCode,
                airlineName = airlineName,
                number = flightNumber,
                fareType = "Economy",
                baggage = "1 Piece(s)",
                duration = segment.duration.toString(),
                aircraft = aircraftName,
                onBoard = "Not Available",
            )
        }
    }.flatten()
}