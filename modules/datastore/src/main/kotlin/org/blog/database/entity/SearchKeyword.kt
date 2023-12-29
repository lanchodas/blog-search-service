package org.blog.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

/**
 * 블로그 검색 키워드와 검색 횟수를 저장하는 엔티티
 *
 * Optimistic Locking(낙관적 락)을 사용하기 위해 version 필드를 추가했습니다.
 */
@Entity
@Table(name = "search_keyword")
class SearchKeyword(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val keyword: String,

    @Column(name = "search_count")
    var searchCount: Long,

    @Version
    val version: Long = 0,

    @Column(name = "updated_at")
    @UpdateTimestamp
    val updatedAt: Instant? = null,

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    val createdAt: Instant? = null
)
