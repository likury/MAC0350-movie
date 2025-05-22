<script lang="ts">
    import { api } from '$lib/api';
    import { onMount } from 'svelte';

    let movies: { id: number; tmdbId: number; title: string; posterPath: string }[] = [];
    let loading = true;
    let error = '';
    let currentPage = 1;

    async function loadMovies(page: number) {
        try {
            loading = true;
            error = '';
            movies = await api.getPopularMovies(page);
        } catch (e) {
            error = 'Failed to load popular movies';
            console.error('Error loading popular movies:', e);
        } finally {
            loading = false;
        }
    }

    onMount(() => {
        loadMovies(currentPage);
    });

    function nextPage() {
        currentPage++;
        loadMovies(currentPage);
    }

    function previousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadMovies(currentPage);
        }
    }
</script>

<div class="container">
    <div class="popular-movies">
        <h1>Popular Movies</h1>

        {#if loading}
            <div class="loading">Loading...</div>
        {:else if error}
            <div class="error">{error}</div>
        {:else}
            <div class="movies-grid">
                {#each movies as movie}
                    <div class="movie-card">
                        <div class="movie-poster">
                            {#if movie.posterPath}
                                <img 
                                    src={`https://image.tmdb.org/t/p/w500${movie.posterPath}`}
                                    alt={`${movie.title} poster`}
                                    loading="lazy"
                                />
                            {:else}
                                <div class="no-poster">No poster available</div>
                            {/if}
                        </div>
                        <div class="movie-info">
                            <h3 class="movie-title">{movie.title}</h3>
                        </div>
                    </div>
                {/each}
            </div>

            <div class="pagination">
                <button 
                    class="pagination-button" 
                    on:click={previousPage} 
                    disabled={currentPage === 1}
                >
                    Previous
                </button>
                <span class="page-number">Page {currentPage}</span>
                <button 
                    class="pagination-button" 
                    on:click={nextPage}
                >
                    Next
                </button>
            </div>
        {/if}
    </div>
</div>

<style>
    .container {
        min-height: 100vh;
        background-color: #14181f;
        color: #fff;
        padding: 2rem;
    }

    .popular-movies {
        max-width: 1200px;
        margin: 0 auto;
    }

    h1 {
        color: #00e054;
        font-size: 2rem;
        margin-bottom: 2rem;
    }

    .loading {
        color: #00e054;
        font-size: 1.2rem;
        text-align: center;
        padding: 2rem;
    }

    .error {
        color: #ff4444;
        text-align: center;
        padding: 2rem;
        background-color: rgba(255, 68, 68, 0.1);
        border-radius: 8px;
    }

    .movies-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 2rem;
        margin-bottom: 2rem;
    }

    .movie-card {
        background-color: #1a1f2b;
        border-radius: 8px;
        overflow: hidden;
        transition: transform 0.2s;
    }

    .movie-card:hover {
        transform: translateY(-5px);
    }

    .movie-poster {
        position: relative;
        padding-top: 150%;
        background-color: #2c3440;
    }

    .movie-poster img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .no-poster {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #9ab;
        text-align: center;
        padding: 1rem;
    }

    .movie-info {
        padding: 1rem;
    }

    .movie-title {
        margin: 0;
        font-size: 1rem;
        color: #fff;
        line-height: 1.4;
    }

    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 1rem;
        margin-top: 2rem;
    }

    .pagination-button {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .pagination-button:hover:not(:disabled) {
        background-color: #00c04b;
    }

    .pagination-button:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }

    .page-number {
        color: #9ab;
        font-size: 1.1rem;
    }
</style> 