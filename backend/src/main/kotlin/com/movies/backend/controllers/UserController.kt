package com.movies.backend.controllers

import com.movies.backend.domain.dto.RegisterUserRequest
import com.movies.backend.domain.dto.UserDto
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.mappers.toDto
import com.movies.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody request: RegisterUserRequest): ResponseEntity<UserDto> {
        val saved = userService.register(
            UserEntity(
                username = request.username,
                email = request.email,
                passwordHash = request.password
            )
        )
        return ResponseEntity.ok(saved.toDto())
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        val user = userService.findById(id)
        return user.map { ResponseEntity.ok(it.toDto()) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserDto> {
        val user = userService.findByUsername(username)
        return user.map { ResponseEntity.ok(it.toDto()) }
            .orElse(ResponseEntity.notFound().build())
    }
}
