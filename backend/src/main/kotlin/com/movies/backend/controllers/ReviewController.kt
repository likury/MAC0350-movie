package com.movies.backend.controllers

import com.movies.backend.domain.dto.ReviewDto
import com.movies.backend.domain.dto.SubmitReviewRequest
import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.mappers.toDto
import com.movies.backend.services.MovieService
import com.movies.backend.services.ReviewService
import com.movies.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
    private val userService: UserService,
    private val movieService: MovieService
) {

    @PostMapping("/user/{userId}/movie/{tmdbId}")
    fun submitReview(
        @PathVariable userId: Long,
        @PathVariable tmdbId: Long,
        @RequestBody request: SubmitReviewRequest
    ): ResponseEntity<ReviewDto> {
        val user = userService.findById(userId).orElseThrow { RuntimeException("User not found") }
        
        // Get movie details from TMDB and save to our database if not exists
        val movieDto = movieService.getMovieDetails(tmdbId)
        val movie = movieService.findByTmdbId(tmdbId) ?: throw RuntimeException("Failed to save movie")

        val saved = reviewService.save(
            ReviewEntity(
                user = user,
                movie = movie,
                content = request.content,
                rating = request.rating
            )
        )

        return ResponseEntity.ok(saved.toDto())
    }

    @GetMapping("/movie/{tmdbId}")
    fun getReviewsByMovie(@PathVariable tmdbId: Long): ResponseEntity<List<ReviewDto>> {
        val movie = movieService.findByTmdbId(tmdbId) ?: return ResponseEntity.notFound().build()
        val reviews = reviewService.getByMovie(movie)
        return ResponseEntity.ok(reviews.map { it.toDto() })
    }

    @GetMapping("/user/{userId}")
    fun getReviewsByUser(@PathVariable userId: Long): ResponseEntity<List<ReviewDto>> {
        val user = userService.findById(userId).orElse(null) ?: return ResponseEntity.notFound().build()
        val reviews = reviewService.getByUser(user)
        return ResponseEntity.ok(reviews.map { it.toDto() })
    }
}
