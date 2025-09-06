package com.melfiky.flights

data class TripRequest(
    val legs: List<Leg>
)

data class Leg(
    val id: Int,
    val origin: String,
    val destination: String,
    val departureDate: String,
)

fun TripRequest.isMultiCity(): Boolean {
    if (legs.size == 1) return false
    if (legs.size > 2) return true

    val first = legs.first()
    val second = legs[1]

    return !(first.origin == second.destination && first.destination == second.origin)
}