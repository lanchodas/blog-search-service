package org.blog.database.repository

import org.blog.database.configuration.DataSourceConfiguration
import org.blog.database.entity.SearchKeyword
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [DataSourceConfiguration::class])
@DataJpaTest
internal class SearchKeywordRepositoryTest {

    @Autowired
    private lateinit var searchKeywordRepository: SearchKeywordRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @DisplayName("검색 키워드가 존재하면 SearchKeyword를 반환한다.")
    @Test
    fun `findByKeyword returns SearchKeyword for given keyword`() {
        val keyword = "푸바오"
        val searchKeyword = SearchKeyword(keyword = keyword, searchCount = 1)
        testEntityManager.persist(searchKeyword)
        testEntityManager.flush()

        val result = searchKeywordRepository.findByKeyword(keyword)

        assertNotNull(result)
        assertEquals(keyword, result?.keyword)
    }

    @DisplayName("검색 키워드가 존재하지 않으면 null을 반환한다.")
    @Test
    fun `findByKeyword returns null`() {
        val keyword = "푸바오"
        val result = searchKeywordRepository.findByKeyword(keyword)
        assertNull(result)
    }

    @DisplayName("SearchKeyword를 검색 횟수(searchCount) 내림차순으로 정렬하여 반환한다.")
    @Test
    fun `findAllByOrderBySearchCountDesc returns SearchKeywords in descending order of searchCount`() {
        // given
        val keyword1 = SearchKeyword(keyword = "강아지", searchCount = 2)
        val keyword2 = SearchKeyword(keyword = "고양이", searchCount = 1)
        val keyword3 = SearchKeyword(keyword = "푸바오", searchCount = 3)
        testEntityManager.persist(keyword1)
        testEntityManager.persist(keyword2)
        testEntityManager.persist(keyword3)
        testEntityManager.flush()

        // when
        val pageable = PageRequest.of(0, 10, Sort.by("searchCount").descending())
        val result = searchKeywordRepository.findAllByOrderBySearchCountDesc(pageable)

        // then
        assertEquals(3, result.size)
        assertEquals(result[0].searchCount, 3) // 푸바오
        assertEquals(result[1].searchCount, 2) // 강아지
        assertEquals(result[2].searchCount, 1) // 고양이
    }

    @DisplayName("결과가 없더라도 null이 아닌 empty list를 반환해야한다.")
    @Test
    fun `findAllByOrderBySearchCountDesc returns an empty list`() {
        // when
        val pageable = PageRequest.of(0, 10, Sort.by("searchCount").descending())
        val result = searchKeywordRepository.findAllByOrderBySearchCountDesc(pageable)

        // then
        assertNotNull(result)
        assertEquals(0, result.size)
    }
}
