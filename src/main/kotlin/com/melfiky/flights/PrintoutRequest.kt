package com.melfiky.flights

data class PrintoutRequest(
    val offerId: String,
    val legs: List<FlightsController.Leg>,
    val passengers: List<PassengerData>
)

data class PassengerData(
    val type: String,
    val title: String? = null,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String? = null
)