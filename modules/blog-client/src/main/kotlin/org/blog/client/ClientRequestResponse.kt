package org.blog.client

import java.time.Instant

interface ClientRequest

interface ClientResponse {
    fun getTotalCount(): Int
    fun isEnd(): Boolean
    fun getSearchDocuments(): List<Document>
}

data class Document(
    val title: String,
    val contents: String,
    val url: String,
    val postedAt: Instant
)
