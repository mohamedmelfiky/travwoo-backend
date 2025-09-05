package com.melfiky.flights.payment

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class IntentionToString(
    val mapper: ObjectMapper
) : Converter<IntentionRequest, String> {

    override fun convert(source: IntentionRequest): String? {
        return mapper.writeValueAsString(source)
    }

}