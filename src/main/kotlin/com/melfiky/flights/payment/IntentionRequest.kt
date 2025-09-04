package com.melfiky.flights.payment

data class IntentionRequest(
    val passengersCount: Int,
    val billingData: BillingData
)
