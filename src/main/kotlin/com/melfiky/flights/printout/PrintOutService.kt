package com.melfiky.flights.printout

import com.lowagie.text.*
import com.lowagie.text.Rectangle.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.melfiky.flights.AmadeusService
import com.melfiky.flights.FlightRepresentation
import com.melfiky.flights.FlightsController.TripRequest
import com.melfiky.flights.payment.IntentionRequest
import com.melfiky.flights.payment.PaymentNotFoundException
import com.melfiky.flights.payment.PaymentNotSuccessException
import com.melfiky.flights.payment.PaymentRepository
import com.melfiky.flights.payment.PaymentStatus
import com.melfiky.flights.toFlightRepresentation
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xhtmlrenderer.resource.XMLResource
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.StringReader
import java.util.*


@Service
class PrintOutService(
    private val amadeusService: AmadeusService,
    private val templateEngine: TemplateEngine,
    private val paymentRepository: PaymentRepository
) {

    private val log = LoggerFactory.getLogger(PrintOutService::class.java)

    fun printout(request: PrintoutRequest): ByteArray? {
        val payment = paymentRepository.findByIdOrNull(request.paymentId) ?: throw PaymentNotFoundException()
//        if (payment.status != PaymentStatus.SUCCESS) throw PaymentNotSuccessException()
        return checkPrintout(payment.data)
    }

    fun checkPrintout(request: IntentionRequest): ByteArray? {
        val response = amadeusService.multiCityFlightOffers(TripRequest(request.legs))
        val offer = response.flights.find { it.id == request.offerId.toInt() } ?: return null

        log.info("Processing $offer")

        return htmlToPdf(
            request.passengers.map { "${it.lastName} / ${it.firstName} ${it.title}" },
            offers = offer.toFlightRepresentation()
        )
    }

    fun htmlToPdf(names: List<String>, offers: List<FlightRepresentation>): ByteArray {
        val amadeusTemplate = File("templates/amadeus_old.html")
        val htmlContent = amadeusTemplate.readLines().joinToString("\n")

        val variables: Map<String, Any> = mapOf(
            "names" to names.map { it.uppercase() },
            "flights" to offers,
        )
        val context = Context(Locale.ENGLISH, variables)
        val processed = templateEngine.process(htmlContent, context)

//        val fileOut = FileOutputStream("templates/ticketHtml.pdf")
        val out = ByteArrayOutputStream()
        val renderer = ITextRenderer()
        renderer.isScaleToFit = true
        val source = XMLResource.load(StringReader(processed)).document
        renderer.createPDF(source, out)
        return out.toByteArray()
    }
}