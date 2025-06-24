package com.movies.backend.services

import com.movies.backend.domain.entities.UserEntity
import java.util.*

interface UserService {
    fun register(userEntity: UserEntity): UserEntity
    fun findByUsername(username: String): Optional<UserEntity>
    fun findById(id: Long): Optional<UserEntity>
}
