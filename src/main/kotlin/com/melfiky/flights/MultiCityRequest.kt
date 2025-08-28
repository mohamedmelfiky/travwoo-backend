package com.melfiky.flights

import com.fasterxml.jackson.annotation.JsonProperty

data class MultiCityRequest(
    val originDestinations: List<OriginDestination>,
    val travelers: List<TravelerInfo> = listOf(TravelerInfo()),
    val sources: List<String> = listOf("GDS"),
    val searchCriteria: SearchCriteria? = null,
)

data class OriginDestination(
    val id: String,
    @JsonProperty(value = "originLocationCode")
    val origin: String,
    @JsonProperty(value = "destinationLocationCode")
    val destination: String,
    @JsonProperty(value = "departureDateTimeRange")
    val departureDate: DateTimeRange,
)

data class DateTimeRange(
    val date: String,
    val time: String? = null,
    val dateWindow: String? = null,
    val timeWindow: String? = null,
)

data class TravelerInfo(
    val id: String = "1",
    val travelerType: String = "ADULT"
)

data class SearchCriteria(
    val maxFlightOffers: Int?,
)