package org.blog.database.service

import org.blog.database.entity.SearchKeyword
import org.blog.database.repository.SearchKeywordRepository
import org.springframework.stereotype.Service

@Service
class SearchKeywordService(
    private val searchKeywordRepository: SearchKeywordRepository
) {
        fun findAll(): List<SearchKeyword> = searchKeywordRepository.findAll()
        fun save(searchKeyword: SearchKeyword) = searchKeywordRepository.save(searchKeyword)
}
