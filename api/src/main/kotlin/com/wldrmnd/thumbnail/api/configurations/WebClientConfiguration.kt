package com.wldrmnd.thumbnail.api.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClient(): WebClient = WebClient.builder().baseUrl("http://localhost:89").build()
}
