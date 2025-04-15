package com.movies.backend.domain.entities

import com.movies.backend.domain.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "follows")
data class FollowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "follow_id_seq")
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    val follower: UserEntity,

    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    val followed: UserEntity,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
