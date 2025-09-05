package com.melfiky.flights.payment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("PAYMENTS")
data class PaymentEntity(
    @Id val id: String? = null,
    val data: IntentionRequest,
    val status: PaymentStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class PaymentStatus {
    PENDING, SUCCESS, FAILED
}