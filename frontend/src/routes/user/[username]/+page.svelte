<script lang="ts">
    import { api } from '$lib/api';
    import { page } from '$app/stores';
    import { onMount } from 'svelte';

    let user: { id: number; username: string; email: string } | null = null;
    let reviews: { id: number; userId: number; movieId: number; tmdbMovieId: number; content: string; rating: number; createdAt: string }[] = [];
    let movieDetails: Map<number, { title: string; posterPath: string }> = new Map();
    let loading = true;
    let error = '';

    async function loadMovieDetails(tmdbId: number) {
        try {
            const movie = await api.getMovieDetails(tmdbId);
            movieDetails.set(tmdbId, {
                title: movie.title,
                posterPath: movie.posterPath
            });
        } catch (e) {
            console.error(`Failed to load movie details for TMDB ID ${tmdbId}:`, e);
        }
    }

    onMount(async () => {
        try {
            const username = $page.params.username;
            const userResponse = await api.getUserByUsername(username);
            user = userResponse;
            
            if (user) {
                const reviewsResponse = await api.getUserReviews(user.id);
                reviews = reviewsResponse;
                
                // Load movie details for each review
                const uniqueTmdbIds = [...new Set(reviews.map(review => review.tmdbMovieId))];
                await Promise.all(uniqueTmdbIds.map(loadMovieDetails));
            }
        } catch (e) {
            error = 'User not found';
        } finally {
            loading = false;
        }
    });
</script>

<div class="container">
    {#if loading}
        <div class="loading">Loading...</div>
    {:else if error}
        <div class="error-container">
            <h1>Oops! 😕</h1>
            <p class="error-message">{error}</p>
            <a href="/register" class="button">Create an Account</a>
        </div>
    {:else if user}
        <div class="profile-container">
            <div class="profile-header">
                <h1>{user.username}'s Profile</h1>
            </div>
            
            <div class="profile-content">
                <div class="info-card">
                    <div class="info-item">
                        <span class="label">User ID</span>
                        <span class="value">{user.id}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">Username</span>
                        <span class="value">{user.username}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">Email</span>
                        <span class="value">{user.email}</span>
                    </div>
                </div>

                <div class="reviews-section">
                    <h2>Reviews</h2>
                    {#if reviews.length === 0}
                        <p class="no-reviews">No reviews yet</p>
                    {:else}
                        <div class="reviews-list">
                            {#each reviews as review}
                                <div class="review-card">
                                    <div class="review-header">
                                        <div class="movie-info">
                                            {#if movieDetails.has(review.tmdbMovieId)}
                                                {@const movie = movieDetails.get(review.tmdbMovieId)}
                                                <div class="movie-title">
                                                    {movie?.title}
                                                </div>
                                            {:else}
                                                <div class="movie-title loading">Loading movie details...</div>
                                            {/if}
                                        </div>
                                        <div class="rating">
                                            {#each Array(5) as _, i}
                                                <span class="star {i < review.rating ? 'filled' : ''}">★</span>
                                            {/each}
                                        </div>
                                    </div>
                                    <p class="review-content">{review.content}</p>
                                    <div class="review-footer">
                                        <span class="review-date">{new Date(review.createdAt).toLocaleDateString()}</span>
                                    </div>
                                </div>
                            {/each}
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    {/if}
</div>

<style>
    .container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #14181f;
        color: #fff;
        padding: 1rem;
    }

    .loading {
        color: #00e054;
        font-size: 1.2rem;
    }

    .error-container {
        background-color: #1a1f2b;
        padding: 2rem;
        border-radius: 8px;
        text-align: center;
        max-width: 400px;
        width: 100%;
    }

    .error-message {
        color: #ff4444;
        margin: 1rem 0;
    }

    .profile-container {
        background-color: #1a1f2b;
        border-radius: 8px;
        width: 100%;
        max-width: 800px;
        overflow: hidden;
    }

    .profile-header {
        background-color: #00e054;
        padding: 2rem;
        text-align: center;
    }

    .profile-header h1 {
        color: #14181f;
        margin: 0;
        font-size: 2rem;
    }

    .profile-content {
        padding: 2rem;
    }

    .info-card {
        background-color: #14181f;
        border-radius: 8px;
        padding: 1.5rem;
        margin-bottom: 2rem;
    }

    .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 0;
        border-bottom: 1px solid #2c3440;
    }

    .info-item:last-child {
        border-bottom: none;
    }

    .label {
        color: #9ab;
        font-size: 0.9rem;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .value {
        color: #fff;
        font-weight: 500;
    }

    .button {
        display: inline-block;
        background-color: #00e054;
        color: #14181f;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        text-decoration: none;
        font-weight: bold;
        margin-top: 1rem;
        transition: background-color 0.2s;
    }

    .button:hover {
        background-color: #00c04b;
    }

    .reviews-section {
        margin-top: 2rem;
    }

    .reviews-section h2 {
        color: #00e054;
        margin-bottom: 1rem;
        font-size: 1.5rem;
    }

    .no-reviews {
        color: #9ab;
        text-align: center;
        padding: 2rem;
        background-color: #14181f;
        border-radius: 8px;
    }

    .reviews-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .review-card {
        background-color: #14181f;
        border-radius: 8px;
        padding: 1.5rem;
    }

    .review-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .movie-info {
        color: #9ab;
    }

    .movie-title {
        color: #fff;
        font-size: 1.1rem;
        font-weight: 500;
    }

    .movie-title.loading {
        color: #9ab;
        font-size: 0.9rem;
    }

    .rating {
        display: flex;
        gap: 0.25rem;
    }

    .star {
        color: #2c3440;
        font-size: 1.2rem;
    }

    .star.filled {
        color: #00e054;
    }

    .review-content {
        color: #fff;
        line-height: 1.5;
        margin: 0;
    }

    .review-footer {
        margin-top: 1rem;
        color: #9ab;
        font-size: 0.9rem;
    }
</style> 