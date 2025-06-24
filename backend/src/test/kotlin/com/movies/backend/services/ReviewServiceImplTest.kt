package com.movies.backend.services

import com.movies.backend.domain.entities.*
import com.movies.backend.repositories.ReviewRepository
import com.movies.backend.services.impl.ReviewServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class ReviewServiceImplTest {

    private val reviewRepository: ReviewRepository = mock()
    private val reviewService = ReviewServiceImpl(reviewRepository)

    private val user = UserEntity(id = 1, username = "test", email = "email", passwordHash = "pass")
    private val movie = MovieEntity(id = 1, tmdbId = 100, title = "Interstellar", posterPath = "")

    @Test
    fun `should save review`() {
        val review = ReviewEntity(user = user, movie = movie, content = "Awesome", rating = 5)
        val savedReview = review.copy(id = 1)

        // Safer with any<ReviewEntity>()
        whenever(reviewRepository.save(any<ReviewEntity>())).thenReturn(savedReview)

        val result = reviewService.save(review)

        assertEquals("Awesome", result.content)
        assertEquals(1, result.id)
        verify(reviewRepository).save(any())
    }

    @Test
    fun `should find reviews by user`() {
        val reviews = listOf(
            ReviewEntity(id = 1, user = user, movie = movie, content = "Cool", rating = 4)
        )
        whenever(reviewRepository.findByUser(user)).thenReturn(reviews)

        val result = reviewService.getByUser(user)

        assertEquals(1, result.size)
        assertEquals("Cool", result.first().content)
        verify(reviewRepository).findByUser(user)
    }

    @Test
    fun `should find reviews by movie`() {
        val reviews = listOf(
            ReviewEntity(id = 1, user = user, movie = movie, content = "Deep", rating = 5)
        )
        whenever(reviewRepository.findByMovie(movie)).thenReturn(reviews)

        val result = reviewService.getByMovie(movie)

        assertEquals(1, result.size)
        assertEquals("Deep", result.first().content)
        verify(reviewRepository).findByMovie(movie)
    }

    @Test
    fun `should find review by id`() {
        val review = ReviewEntity(id = 42, user = user, movie = movie, content = "Legendary", rating = 5)
        whenever(reviewRepository.findById(42)).thenReturn(java.util.Optional.of(review))

        val result = reviewService.getById(42)

        assertEquals("Legendary", result?.content)
        assertEquals(42, result?.id)
        verify(reviewRepository).findById(42)
    }

}
