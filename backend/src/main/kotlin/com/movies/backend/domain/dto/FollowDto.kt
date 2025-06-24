package com.movies.backend.domain.dto

data class FollowDto(
    val id: Long?,
    val followerId: Long,
    val followedId: Long
)
