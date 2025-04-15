package com.movies.backend.domain.entities

import jakarta.persistence.*

@Entity
@Table(name = "movies")
data class MovieEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_id_seq")
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "tmdb_id", nullable = false)
    val tmdbId: Long,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "poster_path")
    val posterPath: String?
)
