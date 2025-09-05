package com.melfiky.flights.payment

import com.melfiky.flights.printout.CreatePrintoutException
import com.melfiky.flights.printout.PrintOutService
import com.melfiky.flights.printout.PrintoutRequest
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class PaymobService(
    private val paymobClient: PaymobClient,
    private val printOutService: PrintOutService,
    private val paymentRepository: PaymentRepository
) {

    private val log = LoggerFactory.getLogger(PaymobService::class.java)
    private val price = 150_00
    private val basePaymentUrl = "https://accept.paymob.com/unifiedcheckout/"
    private val publicKey = "egy_pk_test_7UTgyyDEfvrcZVyGKTOO20eayX2yuBJi"

    fun intention(request: IntentionRequest) : IntentionResponse {
        log.info("Intention request : $request")
        val paymentId = UUID.randomUUID().toString()

        printOutService.checkPrintout(request) ?: throw CreatePrintoutException()

        val paymentEntity = PaymentEntity(
            id = paymentId,
            data = request,
            status = PaymentStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        paymentRepository.insert(paymentEntity)

        val request = PaymobIntentionRequest(
            amount = request.passengers.size * price,
            billingData = PaymobBillingData(
                firstName = request.billingData.firstName,
                lastName = request.billingData.lastName,
                phone = request.billingData.phone,
            ),
            reference = paymentId
        )
        val response = paymobClient.intention(request)
        val secret = response.clientSecret
        val paymentUrl = "$basePaymentUrl?publicKey=$publicKey&clientSecret=$secret"
        log.info(paymentUrl)
        return IntentionResponse(paymentUrl)
    }

    fun onWebhook(webhook: PaymentWebhook) {
        val paymentId = webhook.data.order.merchantOrderId
        val payment = paymentRepository.findByIdOrNull(paymentId) ?: throw PaymentNotFoundException()
        val paymentStatus = if (webhook.data.success) PaymentStatus.SUCCESS else PaymentStatus.FAILED
        val updated = payment.copy(status = paymentStatus, updatedAt = LocalDateTime.now())
        paymentRepository.save(updated)
    }

}