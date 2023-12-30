package org.blog.client.kakao

import feign.FeignException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.blog.client.BlogClientException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KakaoClientTest {
    @MockK
    private lateinit var kakaoApi: KakaoApi

    @InjectMockKs
    private lateinit var kakaoClient: KakaoClient

    @DisplayName("KakaoClient API 호출 시 에러가 발생하면 BlogClientException을 던진다.")
    @Test
    fun `search throws a BlogClientException if an error occurs when calling the KakaoClient API`() {
        every { kakaoApi.search(any<String>(), any<KakaoSortType>(), any<Int>(), any<Int>()) }
            .throws(FeignException.InternalServerError("error", mockk(relaxed = true), "error".toByteArray(), mapOf()))

        val request: KakaoRequest = mockk(relaxed = true)

        assertThrows<BlogClientException> {
            kakaoClient.search(request)
        }
    }
}
