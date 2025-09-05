package com.melfiky.flights.printout

import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.util.*

@RestController
class PrintoutController(private val printOutService: PrintOutService) {

    private val log = LoggerFactory.getLogger(PrintoutController::class.java)

    @PostMapping("printout")
    fun printOut(@RequestBody request: PrintoutRequest): ResponseEntity<InputStreamResource> {
        val bytes = printOutService
            .printout(request)
            ?: return ResponseEntity<InputStreamResource>(HttpStatus.BAD_REQUEST)

        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition
            .inline()
            .filename("${request.paymentId}.pdf")
            .build()

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(InputStreamResource(ByteArrayInputStream(bytes)))
    }

}