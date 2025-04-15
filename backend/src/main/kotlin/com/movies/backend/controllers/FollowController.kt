package com.movies.backend.controllers

import com.movies.backend.domain.dto.FollowDto
import com.movies.backend.mappers.toDto
import com.movies.backend.services.FollowService
import com.movies.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/follows")
class FollowController(
    private val followService: FollowService,
    private val userService: UserService
) {

    @PostMapping("/{followerId}/follow/{followedId}")
    fun followUser(
        @PathVariable followerId: Long,
        @PathVariable followedId: Long
    ): ResponseEntity<FollowDto> {
        val follower = userService.findById(followerId).orElseThrow { IllegalArgumentException("Follower not found") }
        val followed = userService.findById(followedId).orElseThrow { IllegalArgumentException("Followed not found") }

        val follow = followService.follow(follower, followed)
        return ResponseEntity.ok(follow.toDto())
    }

    @GetMapping("/{userId}/followers")
    fun getFollowers(@PathVariable userId: Long): ResponseEntity<List<FollowDto>> {
        val user = userService.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        val followers = followService.getFollowers(user)
        return ResponseEntity.ok(followers.map { it.toDto() })
    }

    @GetMapping("/{userId}/following")
    fun getFollowing(@PathVariable userId: Long): ResponseEntity<List<FollowDto>> {
        val user = userService.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        val following = followService.getFollowing(user)
        return ResponseEntity.ok(following.map { it.toDto() })
    }
}
