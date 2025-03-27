package com.sepeda.bookstore.repositories

import com.sepeda.bookstore.domain.entities.BookEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<BookEntity, String>
