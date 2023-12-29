package org.blog.api.service

import org.blog.database.entity.SearchKeyword
import org.blog.database.service.SearchKeywordService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class KeywordCountService(
    private val searchKeywordService: SearchKeywordService
) {
    fun upsert(keyword: String, count: Long) {
        searchKeywordService.upsertKeywordCount(keyword, count)
    }

    /**
     * datastore 모듈을 통해 인기 검색어 목록을 가져옵니다.
     * Caffeine을 통해 로컬 캐시를 사용합니다. (CacheConfiguration 참고)
     */
    @Cacheable(cacheNames = ["popularKeywords"], key = "'popularKeywordsKey'")
    fun getPopularKeywords(): List<SearchKeyword> {
        return searchKeywordService.getPopularKeywords()
    }
}
