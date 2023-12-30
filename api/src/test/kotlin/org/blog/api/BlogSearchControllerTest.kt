package org.blog.api

import io.mockk.every
import io.mockk.mockk
import org.blog.api.service.BlogSearchService
import org.blog.api.service.KeywordCountService
import org.blog.client.kakao.KakaoDocument
import org.blog.client.kakao.KakaoMeta
import org.blog.client.kakao.KakaoResponse
import org.blog.database.entity.SearchKeyword
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import java.time.Instant

class BlogSearchControllerTest : RestDocsTest() {
    private lateinit var blogSearchService: BlogSearchService
    private lateinit var keywordCountService: KeywordCountService
    private lateinit var blogSearchController: BlogSearchController

    @BeforeEach
    fun setUp() {
        blogSearchService = mockk()
        keywordCountService = mockk()
        blogSearchController = BlogSearchController(blogSearchService, keywordCountService)
        mockMvc = mockMvc(blogSearchController)
    }

    @DisplayName("/search API의 REST Docs를 생성합니다.")
    @Test
    fun `generate search API REST Docs`() {
        every { blogSearchService.search(any()) } returns
            KakaoResponse(
                meta = KakaoMeta(
                    totalCount = 85771,
                    isEnd = false,
                    pageableCount = 798
                ),
                documents = listOf(
                    KakaoDocument(
                        title = "블로그 제목",
                        contents = "블로그 요약 내용",
                        url = "https://pubao.tistory.com/1",
                        blogname = "푸바오 일기장",
                        thumbnail = "https://search.kakaocdn.net/abcdefg",
                        datetime = Instant.now()
                    )
                )
            )

        mockMvc
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .queryParams("query", "Pubao")
            .queryParams("sort", "accuracy")
            .queryParams("page", "1")
            .queryParams("size", "10")
            .get("/api/v1/search")
            .then()
            .assertThat()
            .status(HttpStatus.OK)
            .apply(
                document(
                    "search",
                    Preprocessors.preprocessRequest(
                        Preprocessors.modifyUris().scheme("http").host("localhost").port(8080),
                        Preprocessors.prettyPrint(),
                    ),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    queryParameters(
                        parameterWithName("query").description("Required | 검색 키워드. 최대 20자"),
                        parameterWithName("sort").description("Optional | 정렬 방식 (accuracy: 정확도순, recency: 최신순). 기본값 accuracy"),
                        parameterWithName("page").description("Required | 페이지 번호 (1 ~ 50)"),
                        parameterWithName("size").description("Optional | 한 페이지에 보여줄 문서 개수 (1 ~ 50). 기본값 10")
                    ),
                    responseFields(
                        fieldWithPath("total_count").type(JsonFieldType.NUMBER).description("검색 결과 총 개수"),
                        fieldWithPath("is_end").type(JsonFieldType.BOOLEAN).description("검색 결과 종료 여부"),
                        fieldWithPath("documents").type(JsonFieldType.ARRAY).description("블로그 검색 결과 리스트"),
                        fieldWithPath("documents[].blog_name").type(JsonFieldType.STRING).description("블로그 이름"),
                        fieldWithPath("documents[].title").type(JsonFieldType.STRING).description("블로그 글 제목"),
                        fieldWithPath("documents[].contents").type(JsonFieldType.STRING).description("블로그 글 요약 내용"),
                        fieldWithPath("documents[].url").type(JsonFieldType.STRING).description("블로그 글 URL"),
                        fieldWithPath("documents[].posted_at").type(JsonFieldType.STRING)
                            .description("블로그 글 작성시간. ISO-8601")
                    )
                )
            )
    }

    @DisplayName("/search/popular-keywords API의 REST Docs를 생성합니다.")
    @Test
    fun `generate popular-keywords API REST Docs`() {
        every { keywordCountService.getPopularKeywords() } returns listOf(
            SearchKeyword(keyword = "푸바오", searchCount = 1000),
            SearchKeyword(keyword = "강아지", searchCount = 500)
        )

        mockMvc
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .get("/api/v1/search/popular-keywords")
            .then()
            .assertThat()
            .status(HttpStatus.OK)
            .apply(
                document(
                    "popular-keywords",
                    Preprocessors.preprocessRequest(
                        Preprocessors.modifyUris().scheme("http").host("localhost").port(8080),
                        Preprocessors.prettyPrint(),
                    ),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("인기 검색어 목록"),
                        fieldWithPath("[].keyword").type(JsonFieldType.STRING).description("검색 키워드"),
                        fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("검색 횟수")
                    )
                )
            )
    }
}
