package com.movies.backend.services.impl

import com.movies.backend.domain.entities.LikeEntity
import com.movies.backend.domain.entities.ReviewEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.repositories.LikeRepository
import com.movies.backend.services.LikeService
import org.springframework.stereotype.Service

@Service
class LikeServiceImpl(
    private val likeRepository: LikeRepository
) : LikeService {

    override fun likeReview(user: UserEntity, review: ReviewEntity): LikeEntity {
        val existing = likeRepository.findByUserAndReview(user, review)
        return existing ?: likeRepository.save(LikeEntity(user = user, review = review))
    }

    override fun getLikesForReview(review: ReviewEntity): List<LikeEntity> {
        return likeRepository.findByReview(review)
    }
}
