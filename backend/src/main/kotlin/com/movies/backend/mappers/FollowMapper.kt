package com.movies.backend.mappers

import com.movies.backend.domain.dto.FollowDto
import com.movies.backend.domain.entities.FollowEntity

fun FollowEntity.toDto(): FollowDto = FollowDto(
    id = this.id,
    followerId = this.follower.id!!,
    followedId = this.followed.id!!
)
