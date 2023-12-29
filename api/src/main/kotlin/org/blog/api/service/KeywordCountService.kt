package org.blog.api.service

import org.blog.database.service.SearchKeywordService
import org.springframework.stereotype.Service

@Service
class KeywordCountService(
    private val searchKeywordService: SearchKeywordService
) {
    fun upsert(keyword: String, count: Long) {
        searchKeywordService.upsertKeywordCount(keyword, count)
    }
}
