package com.melfiky.flights

import java.time.Duration
import java.time.LocalDateTime

data class FlightResponse(
    val flights: List<Flight>
)

data class Flight(
    val id: Int,
    val itineraries: List<Itinerary>,
    val isDirect: Boolean,
    val totalStops: Int,
    val totalDuration: Duration
)

data class Itinerary(
    val segments: List<Segment>,
//    val carriers: Set<Airline>,
    val carriers: String,
    val carrierLogo: String?,
    val from: String,
    val to: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: Duration,
    val stops: Int,
    val isDirect: Boolean,
)

data class Segment(
    val id: Int,
    val departureAirport: Airport,
    val departureAt: LocalDateTime,
    val arrivalAirport: Airport,
    val arrivalAt: LocalDateTime,
    val carrier: Airline,
    val operating: Airline? = null,
    val aircraft: Aircraft,
    val number: String,
    val duration: Duration,
    val numberOfStops: Int,
)