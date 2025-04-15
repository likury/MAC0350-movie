package com.movies.backend.services

import com.movies.backend.domain.entities.*
import com.movies.backend.repositories.LikeRepository
import com.movies.backend.services.impl.LikeServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class LikeServiceImplTest {

    private val likeRepository: LikeRepository = mock()
    private val likeService = LikeServiceImpl(likeRepository)

    private val user = UserEntity(id = 1, username = "user", email = "email", passwordHash = "pass")
    private val movie = MovieEntity(id = 1, tmdbId = 1, title = "Movie", posterPath = "")
    private val review = ReviewEntity(id = 1, user = user, movie = movie, content = "Nice", rating = 5)

    @Test
    fun `should like a review if not already liked`() {
        // Simulate no existing like
        whenever(likeRepository.findByUserAndReview(user, review)).thenReturn(null)

        // Return the same object passed to save(...) to avoid null issue
        whenever(likeRepository.save(any<LikeEntity>())).thenAnswer { it.arguments[0] as LikeEntity }

        val result = likeService.likeReview(user, review)

        assertEquals(user, result.user)
        assertEquals(review, result.review)

        verify(likeRepository).findByUserAndReview(user, review)
        verify(likeRepository).save(any())
    }

    @Test
    fun `should return likes for a review`() {
        val like = LikeEntity(user = user, review = review)

        whenever(likeRepository.findByReview(review)).thenReturn(listOf(like))

        val result = likeService.getLikesForReview(review)

        assertEquals(1, result.size)
        assertEquals(user, result.first().user)

        verify(likeRepository).findByReview(review)
    }
}
