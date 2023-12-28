package org.blog.client.kakao

import org.blog.client.ClientRequest
import org.blog.client.ClientResponse
import org.blog.client.Document
import java.time.Instant

enum class KakaoSortType {
    accuracy, // 정확도순
    recency // 최신순
}

data class KakaoRequest(
    val query: String,
    val sort: KakaoSortType,
    val page: Int, // 1 ~ 50 사이. 기본값 1
    val size: Int // 1 ~ 50 사이. 기본값 10
) : ClientRequest

data class KakaoResponse(
    val documents: List<KakaoDocument>,
    val meta: KakaoMeta
) : ClientResponse {
    override fun getTotalCount() = meta.totalCount
    override fun isEnd() = meta.isEnd
    override fun getSearchDocuments(): List<Document> {
        return documents.map {
            Document(
                title = it.title,
                contents = it.contents,
                url = it.url,
                postedAt = it.datetime
            )
        }
    }
}

data class KakaoDocument(
    val title: String,
    val contents: String,
    val url: String,
    val blogname: String,
    val thumbnail: String,
    val datetime: Instant, // 블로그 글 작성시간. ISO-8601
)

data class KakaoMeta(
    val totalCount: Int, // 검색된 문서 수
    val pageableCount: Int, // total_count 중 노출 가능 문서 수
    val isEnd: Boolean // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
)
