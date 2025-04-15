package com.movies.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.movies.backend.config.TestSecurityConfig
import com.movies.backend.domain.dto.RegisterUserRequest
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.services.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*

@WebMvcTest(UserController::class)
@Import(UserControllerTest.Config::class, TestSecurityConfig::class)
class UserControllerTest {

    @TestConfiguration
    class Config {
        @Bean
        fun userService(): UserService = mock()
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userService: UserService

    @Test
    fun `should register a new user`() {
        val request = RegisterUserRequest("lucas", "lucas@email.com", "password")
        val savedUser = UserEntity(1, request.username, request.email, request.password)

        whenever(userService.register(any())).thenReturn(savedUser)

        mockMvc.post("/api/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should return user by id`() {
        val user = UserEntity(1, "lucas", "lucas@email.com", "password")
        whenever(userService.findById(1)).thenReturn(Optional.of(user))

        mockMvc.get("/api/users/1")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `should return 404 if user not found by id`() {
        whenever(userService.findById(99)).thenReturn(Optional.empty())

        mockMvc.get("/api/users/99")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `should return user by username`() {
        val user = UserEntity(1, "lucas", "lucas@email.com", "password")
        whenever(userService.findByUsername("lucas")).thenReturn(Optional.of(user))

        mockMvc.get("/api/users/username/lucas")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `should return 404 if user not found by username`() {
        whenever(userService.findByUsername("ghost")).thenReturn(Optional.empty())

        mockMvc.get("/api/users/username/ghost")
            .andExpect {
                status { isNotFound() }
            }
    }
}
