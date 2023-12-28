package org.blog.api

import jakarta.validation.Valid
import org.blog.api.request.BlogSearchRequest
import org.blog.api.response.BlogSearchResponse
import org.blog.api.service.BlogSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class BlogSearchController(
    private val blogSearchService: BlogSearchService
) {

    /**
     * 블로그 검색 API
     * GET 요청으로 아래 파라미터들을 쿼리 스트링으로 받아 검색 결과를 반환합니다.
     * @param query 검색어
     * @param sort 정렬 방식 (accuracy: 정확도순, recency: 최신순)
     * @param page 페이지 번호 (1 ~ 50)
     * @param size 한 페이지에 보여줄 문서 개수 (1 ~ 50)
     */
    @GetMapping("/search")
    fun search(
        @Valid @ModelAttribute request: BlogSearchRequest
    ): BlogSearchResponse {
        val clientResponse = blogSearchService.search(request)
        return BlogSearchResponse.of(clientResponse)
    }

    @GetMapping("/popular-search-keywords")
    fun popularSearchKeywords(): String {
        return "Hello, /popular-search-keywords"
    }
}
