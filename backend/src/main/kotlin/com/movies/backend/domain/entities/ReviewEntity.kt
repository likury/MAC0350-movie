package com.movies.backend.domain.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
data class ReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_seq")
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    val movie: MovieEntity,

    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
