package org.blog.api.request

import io.mockk.junit5.MockKExtension
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class BlogSearchRequestTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @DisplayName("BlogSearchRequest 정상 케이스 테스트")
    @Test
    fun `BlogSearchRequest with valid data should pass validation`() {
        val request = BlogSearchRequest(
            query = "푸바오",
            sort = SortType.accuracy,
            page = 1,
            size = 10
        )

        val kakaoRequest = request.toKakaoRequest()
        val naverRequest = request.toNaverRequest()

        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())

        assertEquals("푸바오", kakaoRequest.query)
        assertEquals("accuracy", kakaoRequest.sort.name)
        assertEquals(1, kakaoRequest.page)
        assertEquals(10, kakaoRequest.size)
        assertEquals("푸바오", naverRequest.query)
        assertEquals("sim", naverRequest.sort.name)
        assertEquals(1, naverRequest.start)
        assertEquals(10, naverRequest.display)
    }

    @DisplayName("BlogSearchRequest 기본값 정상 세팅 테스트")
    @Test
    fun `BlogSearchRequest with default values should have correct defaults`() {
        val request = BlogSearchRequest(
            query = "test query",
            page = 1
        )

        assertEquals(SortType.accuracy, request.sort) // sort는 기본값이 accuracy
        assertEquals(10, request.size) // size는 기본값이 10
    }

    @DisplayName("BlogSearchRequest 잘못된 값 검증 테스트")
    @Test
    fun `BlogSearchRequest with invalid data should fail validation`() {
        val request1 = BlogSearchRequest(
            query = "", // 빈 검색 키워드가 올 수 없다.
            page = 1
        )
        val request2 = BlogSearchRequest(
            query = "가나다라마가나다라마가나다라마가나다라마A", // 한글 20자가 넘는 검색 키워드가 올 수 없다.
            page = 1
        )
        val request3 = BlogSearchRequest(
            query = "abcdefABCDEFabcdefABCDEFX", // 영문 20자가 넘는 검색 키워드가 올 수 없다.
            page = 1
        )
        val request4 = BlogSearchRequest(
            query = "푸바오",
            page = 0 // page는 최소 1 이상
        )
        val request5 = BlogSearchRequest(
            query = "푸바오",
            page = 51 // page는 최대 50
        )
        val request6 = BlogSearchRequest(
            query = "푸바오",
            page = 51 // page는 최대 50
        )
        val request7 = BlogSearchRequest(
            query = "푸바오",
            page = 1,
            size = 0 // size는 최소 1 이상
        )
        val request8 = BlogSearchRequest(
            query = "푸바오",
            page = 1,
            size = 51 // size는 최대 50
        )

        val violations1 = validator.validate(request1)
        val violations2 = validator.validate(request2)
        val violations3 = validator.validate(request3)
        val violations4 = validator.validate(request4)
        val violations5 = validator.validate(request5)
        val violations6 = validator.validate(request6)
        val violations7 = validator.validate(request7)
        val violations8 = validator.validate(request8)

        assertTrue(violations1.isNotEmpty())
        assertTrue(violations2.isNotEmpty())
        assertTrue(violations3.isNotEmpty())
        assertTrue(violations4.isNotEmpty())
        assertTrue(violations5.isNotEmpty())
        assertTrue(violations6.isNotEmpty())
        assertTrue(violations7.isNotEmpty())
        assertTrue(violations8.isNotEmpty())
    }
}
