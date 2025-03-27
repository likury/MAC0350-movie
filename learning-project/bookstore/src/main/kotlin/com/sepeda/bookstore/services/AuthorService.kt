package com.sepeda.bookstore.services

import com.sepeda.bookstore.domain.entities.AuthorEntity

interface AuthorService {
    fun save(authorEntity: AuthorEntity) : AuthorEntity
}