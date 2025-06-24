package com.movies.backend.services

import com.movies.backend.domain.entities.FollowEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.repositories.FollowRepository
import com.movies.backend.services.impl.FollowServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class FollowServiceImplTest {

    private val followRepository: FollowRepository = mock()
    private val followService = FollowServiceImpl(followRepository)

    private val follower = UserEntity(1, "john", "john@email.com", "hash")
    private val followed = UserEntity(2, "jane", "jane@email.com", "hash")

    @Test
    fun `should follow a user`() {
        val savedFollow = FollowEntity(
            id = 100,
            follower = follower,
            followed = followed
        )

        whenever(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(null)
        whenever(followRepository.save(any<FollowEntity>())).thenReturn(savedFollow)

        val result = followService.follow(follower, followed)

        assertEquals(savedFollow.id, result.id)
        assertEquals(follower, result.follower)
        assertEquals(followed, result.followed)

        verify(followRepository).findByFollowerAndFollowed(follower, followed)
        verify(followRepository).save(any())
    }

    @Test
    fun `should get followers of a user`() {
        val follow = FollowEntity(id = 101, follower = follower, followed = followed)

        whenever(followRepository.findByFollowed(followed)).thenReturn(listOf(follow))

        val result = followService.getFollowers(followed)

        assertEquals(1, result.size)
        assertEquals(follower, result.first().follower)

        verify(followRepository).findByFollowed(followed)
    }

    @Test
    fun `should get following of a user`() {
        val follow = FollowEntity(id = 102, follower = follower, followed = followed)

        whenever(followRepository.findByFollower(follower)).thenReturn(listOf(follow))

        val result = followService.getFollowing(follower)

        assertEquals(1, result.size)
        assertEquals(followed, result.first().followed)

        verify(followRepository).findByFollower(follower)
    }
}
