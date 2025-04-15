package com.movies.backend.services

import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.domain.entities.MovieEntity

interface ReviewService {
    fun save(reviewEntity: ReviewEntity): ReviewEntity
    fun getByUser(user: UserEntity): List<ReviewEntity>
    fun getByMovie(movie: MovieEntity): List<ReviewEntity>
    fun getById(id: Long): ReviewEntity?
}
