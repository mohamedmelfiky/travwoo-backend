package com.melfiky.flights.payment

import com.fasterxml.jackson.annotation.JsonProperty

enum class WebhookType {
    TRANSACTION
}

data class PaymentWebhook(
    val type: WebhookType,
    @param:JsonProperty("obj")
    val data: WebhookData
)

data class WebhookData(
    val id: Long,
    val success: Boolean,
    val order: Order,
    @param:JsonProperty("created_at")
    val createdAt: String
)

data class Order(
    val id: Long,
    @param:JsonProperty("merchant_order_id")
    val merchantOrderId: String,
    @param:JsonProperty("payment_status")
    val paymentStatus: String,
)