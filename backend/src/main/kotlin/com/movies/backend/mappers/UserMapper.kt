package com.movies.backend.mappers

import com.movies.backend.domain.dto.UserDto
import com.movies.backend.domain.entities.UserEntity

fun UserEntity?.toDto(): UserDto {
    requireNotNull(this) { "Cannot convert null UserEntity to UserDto" }
    return UserDto(
        id = this.id ?: 0,
        username = this.username,
        email = this.email,
        createdAt = this.createdAt
    )
}

