package org.blog.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsTest {

    lateinit var mockMvc: MockMvcRequestSpecification
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.restDocumentation = restDocumentation
    }

    protected fun mockMvc(controller: Any): MockMvcRequestSpecification {
        val messageConverter = MappingJackson2HttpMessageConverter(objectMapper())

        val mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .setMessageConverters(messageConverter)
            .build()

        return RestAssuredMockMvc.given().mockMvc(mockMvc)
    }

    private fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    }
}

