package com.melfiky.flights

import com.lowagie.text.*
import com.lowagie.text.Rectangle.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.slf4j.LoggerFactory
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
    private val templateEngine: TemplateEngine,
) {

    private val logger = LoggerFactory.getLogger(PrintOutService::class.java)

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

    fun printOut() {
        val byteOut: ByteArrayOutputStream = ByteArrayOutputStream()
        val fileOut = FileOutputStream("ticket.pdf")

        val document = Document(PageSize.A4, 40F, 40F, 40F, 40F)
        PdfWriter.getInstance(document, fileOut)
        document.open()

        val titleFont: Font = Font(Font.HELVETICA, 16F, Font.BOLD)
        val headerFont: Font = Font(Font.HELVETICA, 11F, Font.BOLD, Color.WHITE)
        val labelFont: Font = Font(Font.HELVETICA, 10F, Font.BOLD)
        val textFont: Font = Font(Font.HELVETICA, 10F)


        // Title
        val title = Paragraph("Electronic Ticket/Itinerary", titleFont)
        title.setAlignment(Element.ALIGN_CENTER)
        title.setSpacingAfter(15F)
        document.add(title)


        // Greeting
        document.add(Paragraph("Dear Valued Customer,", textFont))
        document.add(Paragraph("Please find below your detailed itinerary.", textFont))
        document.add(Chunk.NEWLINE)


        // Traveler Info Header
        addSectionHeader(document, "TRAVELER(S) INFORMATION", headerFont)
        document.add(Paragraph("khafagy / Taghreed Mr", textFont))
        document.add(Chunk.NEWLINE)

        // Booking Info
        addLabelValue(document, "Booking Reservation No:", "U3UR6F", labelFont, textFont)
        addLabelValue(document, "Airline Confirmation No(S):", "AF/U3UR6F", labelFont, textFont)
        document.add(Chunk.NEWLINE)

        // Flight 1
        addFlightSection(
            doc = document,
            route = "Cairo - Paris", date = "Thursday, 17 April, 2025", status = "CONFIRMED",
            leftColumn = listOf(
                listOf("Departure", "02:30\nCairo, Cairo International Airport"),
                listOf("Check-in by", "23:30 (Thursday, 17 April, 2025)"),
                listOf("Airline/Flight", "Air France AF 551"),
                listOf("Fare Type", "Economy"),
                listOf("Baggage", "Not Available")
            ),
            rightColumn = listOf(
                listOf("Arrival", "07:05\nParis, Charles de Gaulle Airport"),
                listOf("Duration", "4h 35m"),
                listOf("Aircraft", "Not Available"),
                listOf("On Board", "Not Available")
            ),
            headerFont = headerFont,
            labelFont = labelFont,
            valueFont = textFont
        )

        document.add(Chunk.NEWLINE)

        // Flight 2
        addFlightSection(
            doc = document,
            route = "Paris - Cairo",
            date = "Monday, 21 April, 2025",
            status = "CONFIRMED",
            leftColumn = listOf(
                listOf("Departure", "20:10\nParis, Charles de Gaulle Airport"),
                listOf("Check-in by", "17:10"),
                listOf("Airline/Flight", "Air France AF 570"),
                listOf("Fare Type", "Economy"),
                listOf("Baggage", "Not Available")
            ),
            rightColumn = listOf(
                listOf("Arrival", "00:30\nCairo, Cairo International Airport"),
                listOf("Duration", "4h 20m"),
                listOf("Aircraft", "Not Available"),
                listOf("On Board", "Not Available")
            ),
            headerFont = headerFont,
            labelFont = labelFont,
            valueFont = textFont
        )

        document.add(Chunk.NEWLINE)
        document.add(Paragraph("Pleased to serve you.", textFont))
        document.add(Paragraph("Best Regards", textFont))

        document.close()
    }

    private fun addSectionHeader(doc: Document, text: String?, font: Font?) {
        val table = PdfPTable(1)
        val cell = PdfPCell(Phrase(text, font))
        cell.setBackgroundColor(Color(11, 60, 111))
        cell.setPadding(6F)
        cell.setBorder(Rectangle.NO_BORDER)
        table.setWidthPercentage(100F)
        table.addCell(cell)
        doc.add(table)
    }

    private fun addLabelValue(doc: Document, label: String?, value: String?, labelFont: Font?, valueFont: Font?) {
        val paragraph = Paragraph()
        paragraph.add(Chunk("$label ", labelFont))
        paragraph.add(Chunk(value, valueFont))
        doc.add(paragraph)
    }

    private fun addFlightSection(
        doc: Document,
        route: String?,
        date: String?,
        status: String?,
        leftColumn: List<List<String>>,
        rightColumn: List<List<String>>,
        headerFont: Font?,
        labelFont: Font?,
        valueFont: Font?
    ) {
        // Header row
        val headerTable = PdfPTable(floatArrayOf(3f, 3f, 2f))
        headerTable.setWidthPercentage(100f)

        val routeCell = PdfPCell(Phrase(route, headerFont))
        routeCell.border = LEFT + TOP
        val dateCell = PdfPCell(Phrase(date, headerFont))
        dateCell.border = TOP
        val statusCell = PdfPCell(Phrase(status, headerFont))
        statusCell.border = RIGHT + TOP

        for (cell in arrayOf(routeCell, dateCell, statusCell)) {
            cell.setBackgroundColor(Color(11, 60, 111))
            cell.setPadding(6f)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
        }

        headerTable.addCell(routeCell)
        headerTable.addCell(dateCell)
        headerTable.addCell(statusCell)
        doc.add(headerTable)

        // Details table
        val detailsTable = PdfPTable(2)
        detailsTable.setWidthPercentage(100f)

        val leftBorder = LEFT + BOTTOM
        val rightBorder = RIGHT + BOTTOM
        detailsTable.addCell(buildFlightColumn(leftColumn, labelFont, valueFont, leftBorder))
        detailsTable.addCell(buildFlightColumn(rightColumn, labelFont, valueFont, rightBorder))

        doc.add(detailsTable)
    }

    private fun buildFlightColumn(
        data: List<List<String>>,
        labelFont: Font?,
        valueFont: Font?,
        border: Int
    ): PdfPCell {
        val innerTable = PdfPTable(1)
        for (row in data) {
            val p = Paragraph()
            p.add(Chunk(row[0] + ": ", labelFont))
            p.add(Chunk(row[1], valueFont))
            val pdfPCell = PdfPCell(p)
            pdfPCell.border = NO_BORDER
            innerTable.addCell(pdfPCell)
        }
        val wrapperCell = PdfPCell(innerTable)
        wrapperCell.setPadding(5f)
        wrapperCell.border = border
        return wrapperCell
    }
}