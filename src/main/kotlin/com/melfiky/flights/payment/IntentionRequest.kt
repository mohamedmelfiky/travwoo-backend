package com.melfiky.flights.payment

import com.melfiky.flights.FlightsController

data class IntentionRequest(
    val offerId: String,
    val legs: List<FlightsController.Leg>,
    val passengers: List<PassengerData>,
    val billingData: BillingData
)

data class PassengerData(
    val type: String,
    val title: String? = null,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String? = null
)