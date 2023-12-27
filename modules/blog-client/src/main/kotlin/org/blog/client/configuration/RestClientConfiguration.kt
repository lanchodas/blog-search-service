package org.blog.client.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient


@Configuration
internal class RestClientConfiguration {

    private fun clientHttpRequestFactory(): ClientHttpRequestFactory {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(3000)
        factory.setReadTimeout(3000)
        return factory
    }

    @Bean
    fun restClient(): RestClient {
        return RestClient.builder()
            .defaultHeaders {
                it.set("Accept", "application/json")
                it.set("Content-Type", "application/json")
            }
            .baseUrl("https://dapi.kakao.com")
            .requestFactory(clientHttpRequestFactory())
            .build()
    }

}
