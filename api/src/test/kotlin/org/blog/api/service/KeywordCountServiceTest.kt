package org.blog.api.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.blog.database.entity.SearchKeyword
import org.blog.database.service.SearchKeywordService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KeywordCountServiceTest {
    private lateinit var mockSearchKeywordService: SearchKeywordService

    private lateinit var keywordCountService: KeywordCountService

    @BeforeEach
    fun setUp() {
        mockSearchKeywordService = mockk(relaxed = true)
        keywordCountService = KeywordCountService(mockSearchKeywordService)
    }

    @DisplayName("upsert 호출 시 구현 로직인 upsertKeywordCount를 호출한다.")
    @Test
    fun `upsert calls upsertKeywordCount of SearchKeywordService`() {
        val keyword = "푸바오"
        val count = 10L
        keywordCountService.upsert(keyword, count)

        every { mockSearchKeywordService.upsertKeywordCount(keyword, count) } returns mockk<SearchKeyword>(relaxed = true)
        verify(exactly = 1) { mockSearchKeywordService.upsertKeywordCount(keyword, count) }
    }

    @DisplayName("getPopularKeywords 호출 시 구현 로직인 getPopularKeywords를 호출한다.")
    @Test
    fun `getPopularKeywords calls getPopularKeywords of SearchKeywordService`() {
        keywordCountService.getPopularKeywords()
        verify(exactly = 1) { mockSearchKeywordService.getPopularKeywords() }
    }
}
