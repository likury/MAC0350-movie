package com.movies.backend.mappers

import com.movies.backend.domain.dto.LikeDto
import com.movies.backend.domain.entities.LikeEntity

fun LikeEntity.toDto() = LikeDto(
    id = this.id!!,
    userId = this.user.id!!,
    reviewId = this.review.id!!,
    createdAt = this.createdAt
)
