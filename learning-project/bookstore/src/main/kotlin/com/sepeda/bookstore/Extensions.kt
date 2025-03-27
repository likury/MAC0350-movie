package com.sepeda.bookstore

import com.sepeda.bookstore.domain.dto.AuthorDto
import com.sepeda.bookstore.domain.entities.AuthorEntity

fun AuthorEntity.toAuthorDto() = AuthorDto(
        id = this.id,
        name = this.name,
        age = this.age,
        description = this.description,
        image = this.image
    )

fun AuthorDto.toAuthorEntity() = AuthorEntity(
    id = this.id,
    name = this.name,
    age = this.age,
    description = this.description,
    image = this.image
)