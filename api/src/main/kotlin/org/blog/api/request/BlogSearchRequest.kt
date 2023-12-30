package org.blog.api.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.blog.client.ClientRequestTransformer
import org.blog.client.kakao.KakaoRequest
import org.blog.client.kakao.KakaoSortType
import org.blog.client.naver.NaverRequest
import org.blog.client.naver.NaverSortType

enum class SortType {
    accuracy, // 정확도순
    recency // 최신순
}

/**
 * page: 카카오는 1 ~ 50 사이. 네이버는 1 ~ 1000 사이입니다. 더 적은 개수 50개로 제한합니다.
 * size: 카카오는 1 ~ 50 사이. 네이버는 10 ~ 100 사이입니다. 더 적은 개수 50개로 제한합니다.
 */
data class BlogSearchRequest(
    @field:NotBlank @field:QueryLength(20) val query: String,
    val sort: SortType? = SortType.accuracy,
    @field:Min(1) @field:Max(50) val page: Int,
    @field:Min(1) @field:Max(50) val size: Int? = 10
) : ClientRequestTransformer {

    override fun toKakaoRequest(): KakaoRequest {
        val kakaoSort = when (sort) {
            SortType.accuracy -> KakaoSortType.accuracy
            SortType.recency -> KakaoSortType.recency
            null -> KakaoSortType.accuracy
        }
        return KakaoRequest(query, kakaoSort, page, size!!)
    }

    override fun toNaverRequest(): NaverRequest {
        val naverSort = when (sort) {
            SortType.accuracy -> NaverSortType.sim
            SortType.recency -> NaverSortType.date
            null -> NaverSortType.sim
        }
        return NaverRequest(query, naverSort, page, size!!)
    }
}
