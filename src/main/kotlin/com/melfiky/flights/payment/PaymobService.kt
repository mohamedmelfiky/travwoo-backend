package com.melfiky.flights.payment

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymobService(private val paymobClient: PaymobClient) {

    private val log = LoggerFactory.getLogger(PaymobService::class.java)
    private val price = 150_00
    private val basePaymentUrl = "https://accept.paymob.com/unifiedcheckout/"
    private val publicKey = "egy_pk_test_7UTgyyDEfvrcZVyGKTOO20eayX2yuBJi"

    fun intention(count: Int, billingData: BillingData) : IntentionResponse {
        log.info("Intention starting with $count and $billingData")
        val request = PaymobIntentionRequest(
            amount = count * price,
            billingData = PaymobBillingData(
                firstName = billingData.firstName,
                lastName = billingData.lastName,
                phone = billingData.phone,
            ),
        )
        val response = paymobClient.intention(request)
        val secret = response.clientSecret
        val paymentUrl = "$basePaymentUrl?publicKey=$publicKey&clientSecret=$secret"
        log.info(paymentUrl)
        return IntentionResponse(paymentUrl)
    }

}