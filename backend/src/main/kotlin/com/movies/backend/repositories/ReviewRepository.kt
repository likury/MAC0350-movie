package com.movies.backend.repositories

import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.domain.entities.MovieEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findByUser(user: UserEntity): List<ReviewEntity>
    fun findByMovie(movie: MovieEntity): List<ReviewEntity>
}
