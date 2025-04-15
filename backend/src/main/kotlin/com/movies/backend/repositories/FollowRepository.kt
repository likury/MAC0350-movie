package com.movies.backend.repositories

import com.movies.backend.domain.entities.FollowEntity
import com.movies.backend.domain.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<FollowEntity, Long> {
    fun findByFollower(follower: UserEntity): List<FollowEntity>
    fun findByFollowed(followed: UserEntity): List<FollowEntity>
    fun findByFollowerAndFollowed(follower: UserEntity, followed: UserEntity): FollowEntity?
}
