package com.melfiky.flights

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface AmadeusApi {

    @GetExchange("v2/shopping/flight-offers")
    fun getFlights(
        @RequestParam("originLocationCode") origin: String,
        @RequestParam("destinationLocationCode") destination: String,
        @RequestParam("departureDate") departureDate: String,
        @RequestParam("returnDate", required = false) returnDate: String?,
        @RequestParam("nonStop") nonStop: Boolean,
        @RequestParam("adults") adults: Int,
    ): FlightOffersSearchResponse

    @PostExchange("v2/shopping/flight-offers")
    fun multiCityFlightOffers(@RequestBody request: MultiCityRequest): FlightOffersSearchResponse

}