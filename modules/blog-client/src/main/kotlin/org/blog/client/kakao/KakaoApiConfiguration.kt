package org.blog.client.kakao

import feign.RequestInterceptor
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 카카오 블로그 검색 API 호출 시 필수 인증 헤더를 추가하기 위한 인터셉터 설정을 합니다.
 * 실제로는 client.kakao.auth-token에 유효한 토큰을 하드코딩하지 않고 Vault와 같은 KMS를 이용할 것이기 때문에
 * jasypt 라이브러리를 이용해 토큰을 decryption 해서 처리하는 것으로 모방만 해두었습니다.
 */
@Configuration
internal class KakaoApiConfiguration {
    @Value("\${client.kakao.auth-token}")
    private lateinit var authToken: String

    @Bean
    fun kakaoRequestInterceptor(encryptor: StringEncryptor): RequestInterceptor {
        return RequestInterceptor {
            it.header("Authorization", "KakaoAK ${encryptor.decrypt(authToken)}")
        }
    }
}
