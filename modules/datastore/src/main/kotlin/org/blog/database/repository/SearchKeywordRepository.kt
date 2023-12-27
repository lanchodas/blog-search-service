package org.blog.database.repository

import org.blog.database.entity.SearchKeyword
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchKeywordRepository : JpaRepository<SearchKeyword, Long> {
}
