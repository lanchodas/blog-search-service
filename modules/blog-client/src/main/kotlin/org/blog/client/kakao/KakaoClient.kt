package org.blog.client.kakao

import feign.FeignException
import org.blog.client.BlogClientException
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "kakao-api", url = "\${client.kakao.url}", configuration = [KakaoApiConfiguration::class])
internal interface KakaoApi {
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/v2/search/blog"],
        consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun search(
        @RequestParam("query") query: String,
        @RequestParam("sort") sort: KakaoSortType,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): KakaoResponse
}

@Component
class KakaoClient internal constructor(
    private val kakaoApi: KakaoApi
) {
    private val logger  = LoggerFactory.getLogger(javaClass)

    fun search(request: KakaoRequest): KakaoResponse {
        val result = try {
            kakaoApi.search(request.query, request.sort, request.page, request.size)
        } catch (e: FeignException) {
            logger.warn("카카오 블로그 검색 호출 에러: ${e.message}")
            throw BlogClientException(e.message ?: e.localizedMessage)
        }

        return result
    }
}
