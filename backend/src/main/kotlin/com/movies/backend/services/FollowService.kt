package com.movies.backend.services

import com.movies.backend.domain.entities.FollowEntity
import com.movies.backend.domain.entities.UserEntity

interface FollowService {
    fun follow(follower: UserEntity, followed: UserEntity): FollowEntity
    fun getFollowers(user: UserEntity): List<FollowEntity>
    fun getFollowing(user: UserEntity): List<FollowEntity>
}
