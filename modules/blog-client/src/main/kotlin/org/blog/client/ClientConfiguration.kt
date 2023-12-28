package org.blog.client

import org.blog.client.kakao.KakaoRequest
import org.blog.client.naver.NaverRequest
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients
internal class ClientConfiguration {
    @Bean
    fun encryptor() = StandardPBEStringEncryptor().also { it.setPassword("kms password") }
}

enum class BlogClients {
    KAKAO_CLIENT,
    NAVER_CLIENT
}

interface ClientRequestTransformer {
    fun toKakaoRequest(): KakaoRequest
    fun toNaverRequest(): NaverRequest
}
