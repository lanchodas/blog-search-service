package org.blog.api.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.blog.api.exception.ApiException
import org.blog.api.exception.ErrorType
import org.blog.api.request.BlogSearchRequest
import org.blog.client.BlogClients
import org.blog.client.ClientResponse
import org.blog.client.kakao.KakaoClient
import org.blog.client.naver.NaverClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BlogSearchService(
    @Value("\${clients.order}") private val clientsOrder: List<BlogClients>,
    private val kakaoClient: KakaoClient,
    private val naverClient: NaverClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * application.yml에 clients.order에 있는 순서 대로 블로그 검색 API를 호출합니다.
     * 기본적으로 첫 번째 순서에 있는 클라이언트를 호출하고, 첫 번째 클라이언트 호출 시 특정 조건에 따라 서킷이 열리면
     * 두 번째 순서에 있는 클라이언트를 호출합니다.
     */
    @CircuitBreaker(name = "blogSearchCircuitBreaker", fallbackMethod = "searchFallback")
    fun search(request: BlogSearchRequest): ClientResponse {
        return getBlogClient(request, 0).invoke()
    }

    /**
     * 서킷이 열렸을 때 호출되는 함수입니다.
     */
    fun searchFallback(request: BlogSearchRequest, e: Throwable): ClientResponse {
        logger.warn("Circuit Opened: ${e.message}")
        return getBlogClient(request, 1).invoke()
    }

    /**
     * 블로그 검색할 클라이언트를 지정하고, 클라이언트를 호출하는 함수를 반환합니다.
     * blog-client 모듈의 kakao, naver 클라이언트는 각각의 블로그 검색에만 역할이 있고,
     * 이 둘을 어떤 순서로 실행할지는 관여하지 않습니다.
     *
     * @param request 블로그 검색 요청 파라미터
     * @param clientOrder 클라이언트 순서
     */
    private fun getBlogClient(request: BlogSearchRequest, clientOrder: Int): () -> ClientResponse {
        if (clientsOrder.isEmpty() || clientsOrder.size < clientOrder) {
            throw ApiException(ErrorType.ILLEGAL_ARGUMENT)
        }
        return when (clientsOrder[clientOrder]) {
            BlogClients.KAKAO_CLIENT -> { { kakaoClient.search(request.toKakaoRequest()) } }
            BlogClients.NAVER_CLIENT -> { { naverClient.search(request.toNaverRequest()) } }
            else -> throw ApiException(ErrorType.ILLEGAL_ARGUMENT)
        }
    }
}
