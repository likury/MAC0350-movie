package com.movies.backend.domain.dto

data class RegisterUserRequest(
    val username: String,
    val email: String,
    val password: String
)
