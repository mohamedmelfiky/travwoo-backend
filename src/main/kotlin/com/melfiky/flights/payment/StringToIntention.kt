package com.melfiky.flights.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class StringToIntention(
    val mapper: ObjectMapper
) : Converter<String, IntentionRequest> {

    override fun convert(source: String): IntentionRequest? {
        return mapper.readValue(source)
    }

}