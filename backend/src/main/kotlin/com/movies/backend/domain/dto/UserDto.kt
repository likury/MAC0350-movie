package com.movies.backend.domain.dto

import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime
)
