package org.blog.client.naver

import feign.FeignException
import org.blog.client.BlogClientException
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "naver-api", url = "\${client.naver.url}", configuration = [NaverApiConfiguration::class])
internal interface NaverApi {
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/v1/search/blog.json"],
        consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun search(
        @RequestParam("query") query: String,
        @RequestParam("sort") sort: NaverSortType,
        @RequestParam("start") start: Int,
        @RequestParam("display") display: Int
    ): NaverResponse
}

@Component
class NaverClient internal constructor(
    private val naverApi: NaverApi
) {
    private val logger  = LoggerFactory.getLogger(javaClass)

    fun search(request: NaverRequest): NaverResponse {
        val result = try {
            naverApi.search(request.query, request.sort, request.start, request.display)
        } catch (e: FeignException) {
            logger.warn("네이버 블로그 검색 호출 에러: ${e.message}")
            throw BlogClientException(e.message ?: e.localizedMessage)
        }

        return result
    }
}
