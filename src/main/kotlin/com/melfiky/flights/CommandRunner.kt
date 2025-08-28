package com.melfiky.flights

import org.slf4j.LoggerFactory

//@Component
class AmadeusRunner(
    private val amadeus: AmadeusService,
    private val printOutService: PrintOutService
) {

    private val logger = LoggerFactory.getLogger(AmadeusRunner::class.java)

    fun run(vararg args: String?) {
//        val result = amadeus.getFlights(
//            origin = "CDG",
//            destination = "JFK",
//            departureDate = "2025-05-30",
//            returnDate = null,
//            nonStop = false,
//        )

//        val flights = result?.data?.first()?.toFlight(result.dictionaries) ?: return
//        logger.info(flights.toString())

//        printOutService.printOut()
//        printOutService.htmlToPdf(
//            listOf(
//                "Elfiky / Mohamed Mr",
//                "Elfiky / Mohamed Mr",
//                "Elfiky / Mohamed Mr",
//                "Elfiky / Mohamed Mr",
//                "Elfiky / Mohamed Mr",
//            ),
//            flights
//        )
    }

}