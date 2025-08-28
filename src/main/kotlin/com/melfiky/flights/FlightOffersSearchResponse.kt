package com.melfiky.flights

data class FlightOffersSearchResponse(
    val meta: Meta,
    val data: List<FlightOffer>,
    val dictionaries: Dictionaries? = null
)

data class Meta(
    val count: Int
)

data class FlightOffer(
    val type: String,
    val id: String,
    val source: String,
    val instantTicketingRequired: Boolean,
    val nonHomogeneous: Boolean,
    val oneWay: Boolean,
    val lastTicketingDate: String,
    val numberOfBookableSeats: Int,
    val itineraries: List<AmadeusItinerary>,
    val price: Price,
    val pricingOptions: PricingOptions,
    val validatingAirlineCodes: List<String>,
    val travelerPricings: List<TravelerPricing>
)

data class AmadeusItinerary(
    val duration: String,
    val segments: List<AmadeusSegment>
)

data class AmadeusSegment(
    val id: String,
    val departure: FlightEndpoint,
    val arrival: FlightEndpoint,
    val carrierCode: String,
    val number: String,
    val aircraft: AmadeusAircraft,
    val operating: OperatingFlight? = null,
    val duration: String,
    val numberOfStops: Int,
    val blacklistedInEU: Boolean
)

data class FlightEndpoint(
    val iataCode: String,
    val at: String // ISO 8601 (YYYY-MM-DDThh:mm:ss)
)

data class AmadeusAircraft(
    val code: String
)

data class OperatingFlight(
    val carrierCode: String
)

data class Price(
    val currency: String,
    val total: String,
    val base: String,
    val grandTotal: String? = null,
)

data class PricingOptions(
    val fareType: List<String>,
    val includedCheckedBagsOnly: Boolean
)

data class TravelerPricing(
    val travelerId: String,
    val fareOption: String,
    val travelerType: TravelerType,
    val price: Price,
    val fareDetailsBySegment: List<FareDetailsBySegment>
)

data class FareDetailsBySegment(
    val segmentId: String,
    val cabin: TravelClass,
    val fareBasis: String,
    val classCode: String? = null,
    val includedCheckedBags: BaggageAllowance? = null
)

data class BaggageAllowance(
    val quantity: Int? = null,
    val weight: Int? = null,
    val weightUnit: String? = null
)

data class Dictionaries(
    val locations: Map<String, Location>? = null,
    val aircraft: Map<String, String>? = null,
    val currencies: Map<String, String>? = null,
    val carriers: Map<String, String>? = null
)

data class Location(
    val cityCode: String,
    val countryCode: String
)

enum class TravelerType {
    ADULT, CHILD, SENIOR, YOUNG, HELD_INFANT, SEATED_INFANT, STUDENT
}

enum class TravelClass {
    ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST
}
