package com.sepeda.bookstore.services.impl

import com.sepeda.bookstore.domain.entities.AuthorEntity
import com.sepeda.bookstore.repositories.AuthorRepository
import com.sepeda.bookstore.services.AuthorService
import org.springframework.stereotype.Service

@Service
class AuthorServiceImpl(private val authorRepository : AuthorRepository): AuthorService {
    override fun save(authorEntity: AuthorEntity): AuthorEntity {
        return authorRepository.save(authorEntity)
    }
}