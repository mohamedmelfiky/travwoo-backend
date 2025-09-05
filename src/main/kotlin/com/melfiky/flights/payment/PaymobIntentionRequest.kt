package com.melfiky.flights.payment

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymobIntentionItem(
    val name: String,
    val amount: Int,
    val description: String,
    val quantity: Int,
)

data class PaymobBillingData(
    @param:JsonProperty(value = "first_name")
    val firstName: String,
    @param:JsonProperty(value = "last_name")
    val lastName: String,
    @param:JsonProperty(value = "phone_number")
    val phone: String,
)

data class PaymobIntentionRequest(
    val amount: Int,
    @param:JsonProperty(value = "billing_data")
    val billingData: PaymobBillingData,
    val currency: String = "EGP",
    @param:JsonProperty(value = "payment_methods")
    val paymentMethods: List<Long> = listOf(5138229),
    @param:JsonProperty(value = "redirection_url")
    val redirectionUrl: String = "tayyarny://close",
    val items: List<PaymobIntentionItem> = listOf(),
    @param:JsonProperty(value = "special_reference")
    val reference: String
)