package org.blog.client.naver

import org.blog.client.ClientRequest
import org.blog.client.ClientResponse
import org.blog.client.Document
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class NaverSortType {
    sim, // 정확도순
    date // 최신순
}

data class NaverRequest(
    val query: String,
    val sort: NaverSortType,
    val start: Int, // page. 1 ~ 1000 사이. 기본값 1
    val display: Int // size. 10 ~ 100 사이. 기본값 10
) : ClientRequest

data class NaverResponse(
    val items: List<NaverItems>,
    val total: Int,
    val start: Int,
    val display: Int,
) : ClientResponse {
    override fun getTotalCount() = total
    override fun isEnd(): Boolean {
        val totalPages = (total + start - 1) / display
        return start >= totalPages
    }
    override fun getSearchDocuments(): List<Document> {
        return items.map {
            Document(
                title = it.title,
                contents = it.description,
                url = it.link,
                postedAt = it.postdate.toInstant()
            )
        }
    }
}

data class NaverItems(
    val title: String,
    val link: String,
    val description: String,
    val bloggername: String,
    val bloggerlink: String,
    val postdate: String, // 블로그 글 작성시간. yyyyMMdd
)

fun String.toInstant(): Instant {
    val date = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyyMMdd"))
    return date.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant()
}
