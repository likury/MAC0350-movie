package com.movies.backend.services.impl

import com.movies.backend.domain.dto.MovieDto
import com.movies.backend.services.TmdbService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class TmdbServiceImpl(
    private val restTemplate: RestTemplate
) : TmdbService {

    @Value("\${tmdb.api.key}")
    private lateinit var apiKey: String

    @Value("\${tmdb.api.base-url}")
    private lateinit var baseUrl: String

    override fun getMovieDetails(tmdbId: Long): MovieDto {
        val url = UriComponentsBuilder
            .fromHttpUrl("$baseUrl/movie/$tmdbId")
            .queryParam("api_key", apiKey)
            .build()
            .toUriString()

        val response = restTemplate.getForObject(url, Map::class.java)
        return mapTmdbResponseToMovieDto(response ?: throw RuntimeException("Failed to fetch movie details"))
    }

    override fun searchMovies(query: String, page: Int): List<MovieDto> {
        val url = UriComponentsBuilder
            .fromHttpUrl("$baseUrl/search/movie")
            .queryParam("api_key", apiKey)
            .queryParam("query", query)
            .queryParam("page", page)
            .build()
            .toUriString()

        val response = restTemplate.getForObject(url, Map::class.java)
        return mapSearchResponseToMovieDtos(response ?: throw RuntimeException("Failed to search movies"))
    }

    override fun getPopularMovies(page: Int): List<MovieDto> {
        val url = UriComponentsBuilder
            .fromHttpUrl("$baseUrl/movie/popular")
            .queryParam("api_key", apiKey)
            .queryParam("page", page)
            .build()
            .toUriString()

        val response = restTemplate.getForObject(url, Map::class.java)
        return mapSearchResponseToMovieDtos(response ?: throw RuntimeException("Failed to fetch popular movies"))
    }

    private fun mapTmdbResponseToMovieDto(response: Map<*, *>): MovieDto {
        return MovieDto(
            id = null,
            tmdbId = (response["id"] as Number).toLong(),
            title = response["title"] as String,
            posterPath = response["poster_path"] as? String
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapSearchResponseToMovieDtos(response: Map<*, *>): List<MovieDto> {
        val results = response["results"] as? List<Map<*, *>> ?: return emptyList()
        return results.map { result ->
            MovieDto(
                id = null,
                tmdbId = (result["id"] as Number).toLong(),
                title = result["title"] as String,
                posterPath = result["poster_path"] as? String
            )
        }
    }
} 