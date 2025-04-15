package com.movies.backend.services

import com.movies.backend.domain.entities.UserEntity
import com.movies.backend.repositories.UserRepository
import com.movies.backend.services.impl.UserServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class UserServiceImplTest {

    private val userRepository: UserRepository = mock()
    private val userService = UserServiceImpl(userRepository)

    @Test
    fun `should register a new user`() {
        val user = UserEntity(username = "testuser", email = "test@example.com", passwordHash = "1234")
        val savedUser = user.copy(id = 1)

        whenever(userRepository.save(any<UserEntity>())).thenReturn(savedUser)

        val result = userService.register(user)

        assertEquals("testuser", result.username)
        assertEquals(1, result.id)
        verify(userRepository).save(any())
    }

    @Test
    fun `should find user by username`() {
        val user = UserEntity(id = 1, username = "johndoe", email = "john@example.com", passwordHash = "pass")
        whenever(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user))

        val found = userService.findByUsername("johndoe")

        assertEquals(true, found.isPresent)
        assertEquals("johndoe", found.get().username)
        verify(userRepository).findByUsername("johndoe")
    }

    @Test
    fun `should find user by id`() {
        val user = UserEntity(id = 1, username = "testuser", email = "test@example.com", passwordHash = "pass")
        whenever(userRepository.findById(1)).thenReturn(Optional.of(user))

        val result = userService.findById(1)

        assertEquals(true, result.isPresent)
        assertEquals("testuser", result.get().username)
        verify(userRepository).findById(1)
    }
}
