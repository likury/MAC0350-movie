package com.movies.backend.services

import com.movies.backend.domain.entities.LikeEntity
import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity

interface LikeService {
    fun likeReview(user: UserEntity, review: ReviewEntity): LikeEntity
    fun getLikesForReview(review: ReviewEntity): List<LikeEntity>
}
