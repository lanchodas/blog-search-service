package org.blog.api.response

import org.blog.database.entity.SearchKeyword

data class KeywordCountResponse(
    val keyword: String,
    val count: Long
) {
    companion object {
        fun of(keywords: List<SearchKeyword>): List<KeywordCountResponse> {
            return keywords.map {
                KeywordCountResponse(it.keyword, it.searchCount)
            }
        }
    }
}
