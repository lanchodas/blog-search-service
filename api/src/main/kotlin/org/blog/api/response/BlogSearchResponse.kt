package org.blog.api.response

import org.blog.client.ClientResponse
import org.blog.client.Document

data class BlogSearchResponse(
    val totalCount: Int,
    val isEnd: Boolean,
    val documents: List<Document>,
) {
    companion object {
        fun of(clientResponse: ClientResponse): BlogSearchResponse {
            return BlogSearchResponse(
                totalCount = clientResponse.getTotalCount(),
                isEnd = clientResponse.isEnd(),
                documents = clientResponse.getSearchDocuments(),
            )
        }
    }
}
