package com.melfiky.flights

import com.fasterxml.jackson.databind.ObjectMapper
import com.melfiky.flights.payment.IntentionToString
import com.melfiky.flights.payment.StringToIntention
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.stereotype.Component

@Component
class JdbcConfiguration(
    private val mapper: ObjectMapper
) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> {
        return listOf(
            IntentionToString(mapper),
            StringToIntention(mapper)
        )
    }

}