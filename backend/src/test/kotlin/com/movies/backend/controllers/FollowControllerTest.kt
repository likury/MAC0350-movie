package com.movies.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.movies.backend.config.TestSecurityConfig
import com.movies.backend.domain.entities.FollowEntity
import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.services.FollowService
import com.movies.backend.services.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
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

@WebMvcTest(FollowController::class)
@Import(FollowControllerTest.Config::class, TestSecurityConfig::class)
class FollowControllerTest {

    @TestConfiguration
    class Config {
        @Bean fun userService(): UserService = mock()
        @Bean fun followService(): FollowService = mock()
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var followService: FollowService

    private val user1 = UserEntity(id = 1, username = "user1", email = "1@email.com", passwordHash = "pass")
    private val user2 = UserEntity(id = 2, username = "user2", email = "2@email.com", passwordHash = "pass")

    @Test
    fun `should follow another user`() {
        val follow = FollowEntity(id = 1, follower = user1, followed = user2)

        whenever(userService.findById(1)).thenReturn(Optional.of(user1))
        whenever(userService.findById(2)).thenReturn(Optional.of(user2))
        whenever(followService.follow(user1, user2)).thenReturn(follow)

        mockMvc.post("/api/follows/1/follow/2") {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should get followers of a user`() {
        val follow = FollowEntity(id = 1, follower = user2, followed = user1)

        whenever(userService.findById(1)).thenReturn(Optional.of(user1))
        whenever(followService.getFollowers(user1)).thenReturn(listOf(follow))

        mockMvc.get("/api/follows/1/followers") {
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should get following list of a user`() {
        val follow = FollowEntity(id = 1, follower = user1, followed = user2)

        whenever(userService.findById(1)).thenReturn(Optional.of(user1))
        whenever(followService.getFollowing(user1)).thenReturn(listOf(follow))

        mockMvc.get("/api/follows/1/following") {
            with(csrf())
        }.andExpect {
            status { isOk() }
        }
    }
}
