package com.movies.backend.repositories

import com.movies.backend.domain.entities.LikeEntity
import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<LikeEntity, Long> {
    fun findByReview(review: ReviewEntity): List<LikeEntity>
    fun findByUserAndReview(user: UserEntity, review: ReviewEntity): LikeEntity?
}
