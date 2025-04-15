package com.movies.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.movies.backend.config.TestSecurityConfig
import com.movies.backend.domain.dto.SubmitLikeRequest
import com.movies.backend.domain.entities.*
import com.movies.backend.services.LikeService
import com.movies.backend.services.ReviewService
import com.movies.backend.services.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(LikeController::class)
@Import(LikeControllerTest.Config::class, TestSecurityConfig::class)
class LikeControllerTest {

    @TestConfiguration
    class Config {
        @Bean fun userService(): UserService = mock()
        @Bean fun reviewService(): ReviewService = mock()
        @Bean fun likeService(): LikeService = mock()
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var reviewService: ReviewService
    @Autowired lateinit var likeService: LikeService

    private val user = UserEntity(id = 1, username = "lucas", email = "lucas@email.com", passwordHash = "pass")
    private val movie = MovieEntity(id = 1, tmdbId = 100, title = "Movie", posterPath = "")
    private val review = ReviewEntity(id = 1, user = user, movie = movie, content = "Nice", rating = 5)
    private val like = LikeEntity(id = 1, user = user, review = review, createdAt = LocalDateTime.now())

    @Test
    fun `should like a review`() {
        val request = SubmitLikeRequest(userId = 1, reviewId = 1)

        whenever(userService.findById(1)).thenReturn(Optional.of(user))
        whenever(reviewService.getById(1)).thenReturn(review)
        whenever(likeService.likeReview(user, review)).thenReturn(like)

        mockMvc.post("/api/likes") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status().isOk
        }
    }

    @Test
    fun `should get likes for review`() {
        whenever(reviewService.getById(1)).thenReturn(review)
        whenever(likeService.getLikesForReview(review)).thenReturn(listOf(like))

        mockMvc.get("/api/likes/review/1") {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status().isOk
        }
    }

    @Test
    fun `should return 404 when review not found`() {
        whenever(reviewService.getById(999)).thenReturn(null)

        mockMvc.get("/api/likes/review/999") {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status().isNotFound
        }
    }
}
