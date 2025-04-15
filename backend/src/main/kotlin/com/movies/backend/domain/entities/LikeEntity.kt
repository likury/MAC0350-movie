package com.movies.backend.domain.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
data class LikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_id_seq")
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    val review: ReviewEntity,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
