package com.movies.backend.controllers

import com.movies.backend.domain.dto.MovieDto
import com.movies.backend.domain.entities.MovieEntity
import com.movies.backend.mappers.toDto
import com.movies.backend.services.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/movies")
class MovieController(
    private val movieService: MovieService
) {

    @PostMapping
    fun saveMovie(@RequestBody movieDto: MovieDto): ResponseEntity<MovieDto> {
        val saved = movieService.save(
            MovieEntity(
                id = null,
                tmdbId = movieDto.tmdbId,
                title = movieDto.title,
                posterPath = movieDto.posterPath
            )
        )
        return ResponseEntity.ok(saved.toDto())
    }

    @GetMapping("/tmdb/{tmdbId}")
    fun getMovieByTmdbId(@PathVariable tmdbId: Long): ResponseEntity<MovieDto> {
        val movie = movieService.findByTmdbId(tmdbId)
        return movie?.let { ResponseEntity.ok(it.toDto()) }
            ?: ResponseEntity.notFound().build()
    }
}
