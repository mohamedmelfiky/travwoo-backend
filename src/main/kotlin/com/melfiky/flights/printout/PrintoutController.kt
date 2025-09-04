package com.melfiky.flights.printout

import com.melfiky.flights.AmadeusService
import com.melfiky.flights.FlightsController.TripRequest
import com.melfiky.flights.toFlightRepresentation
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.util.*

@RestController
class PrintoutController(
    private val amadeusService: AmadeusService,
    private val printOutService: PrintOutService,
) {

    private val log = LoggerFactory.getLogger(PrintoutController::class.java)

    @PostMapping("printout")
    fun printOut(@RequestBody request: PrintoutRequest): ResponseEntity<InputStreamResource> {
        val response = amadeusService.multiCityFlightOffers(TripRequest(request.legs))

        val offer = response.flights
            .find { it.id == request.offerId.toInt() }
            ?: return ResponseEntity<InputStreamResource>(HttpStatus.BAD_REQUEST)

        log.info("Processing $offer")

        val bytes = printOutService.htmlToPdf(
            request.passengers.map { "${it.lastName} / ${it.firstName} ${it.title}" },
            offers = offer.toFlightRepresentation()
        )

        val headers = HttpHeaders()
        val uuid = UUID.randomUUID()
        headers.contentDisposition = ContentDisposition
            .inline()
            .filename("$uuid.pdf")
            .build()

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(InputStreamResource(ByteArrayInputStream(bytes)))
//            .body(InputStreamResource(ByteArrayInputStream("".toByteArray())))
    }

}