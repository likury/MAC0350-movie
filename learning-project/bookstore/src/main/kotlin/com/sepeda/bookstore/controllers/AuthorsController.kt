package com.sepeda.bookstore.controllers

import com.sepeda.bookstore.domain.dto.AuthorDto
import com.sepeda.bookstore.services.AuthorService
import com.sepeda.bookstore.toAuthorDto
import com.sepeda.bookstore.toAuthorEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorsController(private val authorService: AuthorService) {

    @PostMapping(path=["/v1/authors"])
    fun createAuthor(@RequestBody authorDto: AuthorDto): AuthorDto {
        return authorService.save(
            authorDto.toAuthorEntity()
        ).toAuthorDto()
    }
}