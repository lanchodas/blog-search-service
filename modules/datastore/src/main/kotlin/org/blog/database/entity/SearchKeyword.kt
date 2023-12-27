package org.blog.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "search_keyword")
class SearchKeyword(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val keyword: String,

    @Column(name = "search_count")
    val searchCount: Long,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: Instant,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant
)
