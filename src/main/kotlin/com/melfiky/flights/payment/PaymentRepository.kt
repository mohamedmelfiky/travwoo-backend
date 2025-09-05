package com.melfiky.flights.payment

import com.melfiky.flights.WithInsert
import org.springframework.data.repository.ListCrudRepository

interface PaymentRepository : ListCrudRepository<PaymentEntity, String>, WithInsert<PaymentEntity> {
}