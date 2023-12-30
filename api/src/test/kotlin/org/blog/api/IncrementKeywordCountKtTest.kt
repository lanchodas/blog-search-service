package org.blog.api

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.blog.api.service.KeywordCountService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class IncrementKeywordCountKtTest {

    @BeforeEach
    fun setUp() {
        KEYWORD_COUNT_MAP.clear()
    }

    @DisplayName("검색 키워드를 KEYWORD_COUNT_MAP에 횟수와 함께 저장한다.")
    @Test
    fun `incrementKeywordCount increments the count of the keyword`() = runBlocking {
        val keyword1 = "강아지"
        val keyword2 = "고양이"
        val keyword3 = "푸바오"

        incrementKeywordCount(keyword1) {}
        incrementKeywordCount(keyword2) {}
        incrementKeywordCount(keyword3) {}
        incrementKeywordCount(keyword3) {}
        incrementKeywordCount(keyword1) {}
        incrementKeywordCount(keyword3) {}

        withContext(Dispatchers.IO) {
            assertEquals(2, KEYWORD_COUNT_MAP[keyword1])
            assertEquals(1, KEYWORD_COUNT_MAP[keyword2])
            assertEquals(3, KEYWORD_COUNT_MAP[keyword3])
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
