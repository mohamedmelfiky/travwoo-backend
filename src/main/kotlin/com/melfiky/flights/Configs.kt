package com.melfiky.flights

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.templateresolver.StringTemplateResolver

@Configuration
class Configs {

    @Bean
    fun resolver(): StringTemplateResolver {
        return StringTemplateResolver()
    }

}