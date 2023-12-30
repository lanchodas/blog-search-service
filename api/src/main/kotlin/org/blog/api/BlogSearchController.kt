package org.blog.api

import jakarta.validation.Valid
import org.blog.api.request.BlogSearchRequest
import org.blog.api.response.BlogSearchResponse
import org.blog.api.response.KeywordCountResponse
import org.blog.api.service.BlogSearchService
import org.blog.api.service.KeywordCountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class BlogSearchController(
    private val blogSearchService: BlogSearchService,
    private val keywordCountService: KeywordCountService
) {

    /**
     * 블로그 검색 API
     *
     * GET 요청으로 아래 파라미터들을 쿼리 스트링으로 받아 검색 결과를 반환합니다.
     * incrementKeywordCount를 통해 검색 키워드 카운트를 증가시킵니다.
     *
     * @param query 검색 키워드
     * @param sort 정렬 방식 (accuracy: 정확도순, recency: 최신순)
     * @param page 페이지 번호 (1 ~ 50)
     * @param size 한 페이지에 보여줄 문서 개수 (1 ~ 50)
     */
    @GetMapping("/search")
    fun search(
        @Valid @ModelAttribute request: BlogSearchRequest
    ): BlogSearchResponse {
        return incrementKeywordCount(request.query) {
            val clientResponse = blogSearchService.search(request)
            BlogSearchResponse.of(clientResponse)
        }
    }

    /**
     * 인기 검색어 목록 API
     * 많이 검색한 순서 대로 최대 10개의 검색 키워드를 제공합니다.
     */
    @GetMapping("/search/popular-keywords")
    fun popularKeywords(): List<KeywordCountResponse> {
        return KeywordCountResponse.of(keywordCountService.getPopularKeywords())
    }
}
