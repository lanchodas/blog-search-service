package org.blog.api.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.blog.api.exception.ApiException
import org.blog.api.request.BlogSearchRequest
import org.blog.client.BlogClients
import org.blog.client.kakao.KakaoClient
import org.blog.client.kakao.KakaoResponse
import org.blog.client.naver.NaverClient
import org.blog.client.naver.NaverResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class BlogSearchServiceTest {
    @MockK
    private lateinit var kakaoClient: KakaoClient

    @MockK
    private lateinit var naverClient: NaverClient

    private lateinit var blogSearchService: BlogSearchService

    @RelaxedMockK
    private lateinit var request: BlogSearchRequest

    @DisplayName("KakaoClient, NaverClient 순서로 호출할 때, KakaoClient를 호출한다.")
    @Test
    fun `getBlogClient returns KakaoClient when KAKAO_CLIENT is first in clientsOrder`() {
        // given
        val clientsOrder = listOf(BlogClients.KAKAO_CLIENT, BlogClients.NAVER_CLIENT)
        blogSearchService = BlogSearchService(clientsOrder, kakaoClient, naverClient)

        val mockKakaoResponse: KakaoResponse = mockk(relaxed = true)
        every { kakaoClient.search(any()) } returns mockKakaoResponse

        // when
        blogSearchService.search(request)

        // then
        verify(exactly = 1) { kakaoClient.search(any()) }
        verify(exactly = 0) { naverClient.search(any()) }
    }

    @DisplayName("clientsOrder가 비어있으면 ApiException이 발생한다.")
    @Test
    fun `getBlogClient throws ApiException when clientsOrder is empty`() {
        val clientsOrder = emptyList<BlogClients>()
        blogSearchService = BlogSearchService(clientsOrder, kakaoClient, naverClient)
        assertThrows<ApiException> { blogSearchService.search(request) }
    }

    /**
     * 서킷 브레이커 동작 자체를 테스트하는 것은 아니고, 서킷 브레이커가 정상 동작한다면 searchFallback이 호출될 것이고,
     * 이때 clientsOrder에 두 번째로 지정한 NaverClient가 호출되는지를 테스트 합니다.
     */
    @DisplayName("서킷이 열렸을 때, searchFallback이 호출되고, NaverClient를 호출한다.")
    @Test
    fun `searchFallback and NaverClient is called when the circuit is opened`() {
        // given
        val clientsOrder = listOf(BlogClients.KAKAO_CLIENT, BlogClients.NAVER_CLIENT)
        blogSearchService = BlogSearchService(clientsOrder, kakaoClient, naverClient)

        val mockNaverResponse: NaverResponse = mockk(relaxed = true)
        every { naverClient.search(any()) } returns mockNaverResponse

        val mockThrowable: Throwable = mockk(relaxed = true)
        every { mockThrowable.message } returns "KakaoClient Exception"

        // when
        blogSearchService.searchFallback(request, mockThrowable)

        // then
        verify(exactly = 0) { kakaoClient.search(any()) }
        verify(exactly = 1) { naverClient.search(any()) }
    }
}
