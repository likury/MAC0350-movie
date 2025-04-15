package com.movies.backend.services.impl

import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.repositories.UserRepository
import com.movies.backend.services.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun register(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    override fun findByUsername(username: String): Optional<UserEntity> {
        return userRepository.findByUsername(username)
    }

    override fun findById(id: Long): Optional<UserEntity> {
        return userRepository.findById(id)
    }
}
