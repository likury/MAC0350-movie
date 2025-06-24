package com.movies.backend.services.impl

import com.movies.backend.domain.entities.FollowEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.repositories.FollowRepository
import com.movies.backend.services.FollowService
import org.springframework.stereotype.Service

@Service
class FollowServiceImpl(
    private val followRepository: FollowRepository
) : FollowService {

    override fun follow(follower: UserEntity, followed: UserEntity): FollowEntity {
        val existing = followRepository.findByFollowerAndFollowed(follower, followed)
        return existing ?: followRepository.save(FollowEntity(follower = follower, followed = followed))
    }

    override fun getFollowers(user: UserEntity): List<FollowEntity> {
        return followRepository.findByFollowed(user)
    }

    override fun getFollowing(user: UserEntity): List<FollowEntity> {
        return followRepository.findByFollower(user)
    }
}
