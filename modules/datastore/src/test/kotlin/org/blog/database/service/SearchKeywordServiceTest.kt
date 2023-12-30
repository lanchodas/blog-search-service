package org.blog.database.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.OptimisticLockException
import org.blog.database.entity.SearchKeyword
import org.blog.database.repository.SearchKeywordRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.retry.support.RetryTemplate

@ExtendWith(MockKExtension::class)
internal class SearchKeywordServiceTest {
    @MockK
    private lateinit var searchKeywordRepository: SearchKeywordRepository

    @MockK
    private lateinit var retryTemplate: RetryTemplate

    @InjectMockKs
    private lateinit var searchKeywordService: SearchKeywordService

    private val mockSearchKeyword = mockk<SearchKeyword>(relaxed = true)

    @DisplayName("upsertKeywordCount는 retryTemplate을 통해 실행되어야 한다.")
    @Test
    fun `upsertKeywordCount should be executed via retryTemplate`() {
        every { retryTemplate.execute<SearchKeyword, OptimisticLockException>(any()) } returns mockSearchKeyword

        searchKeywordService.upsertKeywordCount("푸바오", 10)

        verify(exactly = 1) {
            retryTemplate.execute<SearchKeyword, OptimisticLockException>(any())
        }
    }

    @DisplayName("존재하지 않는 키워드라면 신규 카운트와 함께 추가한다.")
    @Test
    fun `updateKeywordCount insert new keyword whe no found`() {
        // given
        val keyword = "푸바오"
        val count = 10L
        every { searchKeywordRepository.findByKeyword(keyword) } returns null
        every { searchKeywordRepository.save(any()) } returnsArgument 0

        // when
        SearchKeywordService(searchKeywordRepository, RetryTemplate()).upsertKeywordCount(keyword, count)

        // then
        verify(exactly = 1) {
            searchKeywordRepository.save(match {
                it.keyword == keyword && it.searchCount == count
            })
        }
    }

    @DisplayName("존재하는 키워드라면 기존 카운트에 count를 더해 업데이트한다.")
    @Test
    fun `upsertKeywordCount updates existing keyword when found`() {
        // given
        val keyword = "푸바오"
        val count = 10L
        val existingSearchKeyword = SearchKeyword(id = 1, keyword = keyword, searchCount = 90L)
        every { searchKeywordRepository.findByKeyword(keyword) } returns existingSearchKeyword
        every { searchKeywordRepository.save(any()) } returnsArgument 0

        // when
        SearchKeywordService(searchKeywordRepository, RetryTemplate()).upsertKeywordCount(keyword, count)

        // then
        verify(exactly = 1) {
            searchKeywordRepository.save(match {
                it.keyword == keyword && it.searchCount == 100L // 기존 90 + 10
            })
        }
    }

    @DisplayName("searchCount DESC로 10개의 SearchKeyword를 리턴한다.")
    @Test
    fun `getPopularKeywords returns SearchKeywords in descending order of searchCount`() {
        // given
        val pageRequest = PageRequest.of(0, 10, Sort.by("searchCount").descending())
        every { searchKeywordRepository.findAllByOrderBySearchCountDesc(any()) } returns listOf()

        // when
        searchKeywordService.getPopularKeywords()

        // then
        verify {
            searchKeywordRepository.findAllByOrderBySearchCountDesc(pageRequest)
        }
    }
}
