package com.movies.backend.services.impl

import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.repositories.ReviewRepository
import com.movies.backend.services.ReviewService
import org.springframework.stereotype.Service

@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository
) : ReviewService {

    override fun save(reviewEntity: ReviewEntity): ReviewEntity {
        return reviewRepository.save(reviewEntity)
    }

    override fun getByUser(user: UserEntity): List<ReviewEntity> {
        return reviewRepository.findByUser(user)
    }

    override fun getByMovie(movie: MovieEntity): List<ReviewEntity> {
        return reviewRepository.findByMovie(movie)
    }

    override fun getById(id: Long): ReviewEntity? = reviewRepository.findById(id).orElse(null)

}
