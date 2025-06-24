package com.movies.backend.controllers

import com.movies.backend.domain.dto.LikeDto
import com.movies.backend.domain.dto.SubmitLikeRequest
import com.movies.backend.mappers.toDto
import com.movies.backend.services.LikeService
import com.movies.backend.services.ReviewService
import com.movies.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/likes")
class LikeController(
    private val likeService: LikeService,
    private val reviewService: ReviewService,
    private val userService: UserService
) {

    @PostMapping
    fun likeReview(@RequestBody request: SubmitLikeRequest): ResponseEntity<LikeDto> {
        val user = userService.findById(request.userId).orElseThrow {
            IllegalArgumentException("User with ID ${request.userId} not found.")
        }

        val review = reviewService.getById(request.reviewId)
            ?: return ResponseEntity.notFound().build()

        val like = likeService.likeReview(user, review)
        return ResponseEntity.ok(like.toDto())
    }

    @GetMapping("/review/{reviewId}")
    fun getLikesForReview(@PathVariable reviewId: Long): ResponseEntity<List<LikeDto>> {
        val review = reviewService.getById(reviewId)
            ?: return ResponseEntity.notFound().build()

        val likes = likeService.getLikesForReview(review)
        return ResponseEntity.ok(likes.map { it.toDto() })
    }
}
