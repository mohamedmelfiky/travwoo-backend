package com.melfiky.flights.payment

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    private val paymentService: PaymobService
) {

    @PostMapping("v1/payment/intention")
    fun intention(@RequestBody intention: IntentionRequest): IntentionResponse {
        return paymentService.intention(intention.passengersCount, intention.billingData)
    }

}