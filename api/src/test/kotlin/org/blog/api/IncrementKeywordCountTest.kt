package org.blog.api

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.blog.api.request.BlogSearchRequest
import org.blog.api.service.KeywordCountService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class IncrementKeywordCountTest {

    @BeforeEach
    fun setUp() {
        KEYWORD_COUNT_MAP.clear()
    }

    @DisplayName("검색 키워드를 KEYWORD_COUNT_MAP에 횟수와 함께 저장한다.")
    @Test
    fun `incrementKeywordCount increments the count of the keyword`() = runBlocking {
        val request1 = BlogSearchRequest(query = "강아지", page = 1)
        val request2 = BlogSearchRequest(query = "고양이", page = 1)
        val request3 = BlogSearchRequest(query = "푸바오", page = 1)

        incrementKeywordCount(request1) {}
        incrementKeywordCount(request2) {}
        incrementKeywordCount(request3) {}
        incrementKeywordCount(request3) {}
        incrementKeywordCount(request1) {}
        incrementKeywordCount(request3) {}

        withContext(Dispatchers.IO) {
            assertEquals(2, KEYWORD_COUNT_MAP[request1.query])
            assertEquals(1, KEYWORD_COUNT_MAP[request2.query])
            assertEquals(3, KEYWORD_COUNT_MAP[request3.query])
        }
    }

    @DisplayName("page = 2 이상인 검색은 새로운 검색이 아니므로 KEYWORD_COUNT_MAP에 저장하지 않는다.")
    @Test
    fun `Increment the search count only when page is 1`() = runBlocking {
        val request1 = BlogSearchRequest(query = "푸바오", page = 1)
        val request2 = BlogSearchRequest(query = "푸바오", page = 2)

        incrementKeywordCount(request1) {}
        incrementKeywordCount(request2) {}

        withContext(Dispatchers.IO) {
            assertEquals(1, KEYWORD_COUNT_MAP[request1.query])
        }
    }

    @DisplayName("KEYWORD_COUNT_MAP에 저장된 검색 키워드를 데이터베이스에 저장하고 KEYWORD_COUNT_MAP에서는 제거한다.")
    @Test
    fun `upsertKeywordCount saves the keywords and count in KEYWORD_COUNT_MAP to the database`() {
        val keywordCountService = mockk<KeywordCountService>()
        val scheduledTask = ScheduledTask(keywordCountService)

        KEYWORD_COUNT_MAP["강아지"] = 2
        KEYWORD_COUNT_MAP["고양이"] = 1
        KEYWORD_COUNT_MAP["푸바오"] = 3

        every { keywordCountService.upsert(any<String>(), any<Long>()) } returns Unit

        scheduledTask.upsertKeywordCount()

        verify(exactly = 1) { keywordCountService.upsert("강아지", 2) }
        verify(exactly = 1) { keywordCountService.upsert("고양이", 1) }
        verify(exactly = 1) { keywordCountService.upsert("푸바오", 3) }
        assertEquals(0, KEYWORD_COUNT_MAP.size)
    }
}
