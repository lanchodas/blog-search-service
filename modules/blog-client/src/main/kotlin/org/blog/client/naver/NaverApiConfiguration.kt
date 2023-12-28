package org.blog.client.naver

import feign.RequestInterceptor
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 네이버 블로그 검색 API 호출 시 필수 인증 헤더를 추가하기 위한 인터셉터 설정을 합니다.
 */
@Configuration
internal class NaverApiConfiguration {
    @Value("\${client.naver.client-id}")
    private lateinit var clientId: String

    @Value("\${client.naver.client-secret}")
    private lateinit var clientSecret: String

    @Bean
    fun naverRequestInterceptor(encryptor: StringEncryptor): RequestInterceptor {
        return RequestInterceptor {
            it.header("X-Naver-Client-Id", encryptor.decrypt(clientId))
            it.header("X-Naver-Client-Secret", encryptor.decrypt(clientSecret))
        }
    }
}
