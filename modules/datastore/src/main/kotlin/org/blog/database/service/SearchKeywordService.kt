package org.blog.database.service

import jakarta.persistence.OptimisticLockException
import org.blog.database.entity.SearchKeyword
import org.blog.database.repository.SearchKeywordRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SearchKeywordService(
    private val searchKeywordRepository: SearchKeywordRepository,
    private val retryTemplate: RetryTemplate
) {

    /**
     * SearchKeyword 저장에 낙관적 락을 사용하기 때문에 OptimisticLockException이 발생할 수 있습니다.
     * 이 예외 발생 시 RetryTemplate을 통해 설정된 시간만큼 재시도 합니다.
     */
    @Transactional
    fun upsertKeywordCount(keyword: String, count: Long): SearchKeyword {
        return retryTemplate.execute<SearchKeyword, OptimisticLockException> {
            val searchKeyword = searchKeywordRepository.findByKeyword(keyword)
            if (searchKeyword == null) {
                searchKeywordRepository.save(SearchKeyword(keyword = keyword, searchCount = count))
            } else {
                searchKeyword.searchCount += count
                searchKeywordRepository.save(searchKeyword)
            }
        }
    }

    fun getPopularKeywords(): List<SearchKeyword> {
        return searchKeywordRepository.findAllByOrderBySearchCountDesc(
            PageRequest.of(0, 10, Sort.by("searchCount").descending())
        )
    }
}
