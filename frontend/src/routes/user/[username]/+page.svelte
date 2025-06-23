<script lang="ts">
    import { api } from '$lib/api';
    import { page } from '$app/stores';
    import { onMount } from 'svelte';

    let user: { id: number; username: string; email: string } | null = null;
    let reviews: { id: number; userId: number; movieId: number; tmdbMovieId: number; content: string; rating: number; createdAt: string }[] = [];
    let movieDetails: Map<number, { title: string; posterPath: string }> = new Map();
    let loading = true;
    let error = '';

    // Review form state
    let showReviewForm = false;
    let searchQuery = '';
    let searchResults: { id: number; tmdbId: number; title: string; posterPath: string }[] = [];
    let selectedMovie: { id: number; tmdbId: number; title: string; posterPath: string } | null = null;
    let reviewContent = '';
    let reviewRating = 5;
    let submittingReview = false;
    let searchError = '';
    let searchLoading = false;

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

    async function searchMovies() {
        if (!searchQuery.trim()) return;
        
        try {
            searchLoading = true;
            searchError = '';
            searchResults = await api.searchMovies(searchQuery);
        } catch (e) {
            searchError = 'Failed to search movies';
            console.error('Error searching movies:', e);
        } finally {
            searchLoading = false;
        }
    }

    async function selectMovie(movie: { id: number; tmdbId: number; title: string; posterPath: string }) {
        selectedMovie = movie;
        searchResults = [];
        searchQuery = '';
    }

    async function submitReview() {
        if (!selectedMovie || !user) return;

        try {
            submittingReview = true;
            error = '';
            await api.submitReview(user.id, selectedMovie.tmdbId, reviewContent, reviewRating);
            
            // Reset form
            selectedMovie = null;
            reviewContent = '';
            reviewRating = 5;
            showReviewForm = false;

            // Reload reviews
            const reviewsResponse = await api.getUserReviews(user.id);
            reviews = reviewsResponse.sort((a, b) => 
                new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
            );
            
            // Load movie details for new reviews
            const uniqueTmdbIds = [...new Set(reviews.map(review => review.tmdbMovieId))];
            await Promise.all(uniqueTmdbIds.map(loadMovieDetails));
        } catch (e) {
            error = 'Failed to submit review';
            console.error('Error submitting review:', e);
        } finally {
            submittingReview = false;
        }
    }

    onMount(async () => {
        try {
            const username = $page.params.username;
            const userResponse = await api.getUserByUsername(username);
            user = userResponse;
            
            if (user) {
                const reviewsResponse = await api.getUserReviews(user.id);
                reviews = reviewsResponse.sort((a, b) => 
                    new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                );
                
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

            <div class="profile-navigation">
                <a href="/user/{user.username}" class="nav-link active">Profile</a>
                <a href="/user/{user.username}/social" class="nav-link">Social</a>
                <a href="/user/{user.username}/feed" class="nav-link">Feed</a>
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
                    <div class="reviews-header">
                        <h2>Reviews</h2>
                        <button class="add-review-button" on:click={() => showReviewForm = !showReviewForm}>
                            {showReviewForm ? 'Cancel' : 'Write a Review'}
                        </button>
                    </div>

                    {#if showReviewForm}
                        <div class="review-form">
                            {#if !selectedMovie}
                                <div class="movie-search">
                                    <div class="search-input">
                                        <input
                                            type="text"
                                            bind:value={searchQuery}
                                            placeholder="Search for a movie..."
                                            on:keydown={(e) => e.key === 'Enter' && searchMovies()}
                                        />
                                        <button on:click={searchMovies} disabled={searchLoading}>
                                            {searchLoading ? 'Searching...' : 'Search'}
                                        </button>
                                    </div>

                                    {#if searchError}
                                        <div class="search-error">{searchError}</div>
                                    {/if}

                                    {#if searchResults.length > 0}
                                        <div class="search-results">
                                            {#each searchResults as movie}
                                                <div class="search-result-item" on:click={() => selectMovie(movie)}>
                                                    {#if movie.posterPath}
                                                        <img 
                                                            src={`https://image.tmdb.org/t/p/w92${movie.posterPath}`}
                                                            alt={`${movie.title} poster`}
                                                            loading="lazy"
                                                        />
                                                    {:else}
                                                        <div class="no-poster-small">No poster</div>
                                                    {/if}
                                                    <span class="movie-title">{movie.title}</span>
                                                </div>
                                            {/each}
                                        </div>
                                    {/if}
                                </div>
                            {:else}
                                <div class="selected-movie">
                                    <div class="selected-movie-info">
                                        {#if selectedMovie.posterPath}
                                            <img 
                                                src={`https://image.tmdb.org/t/p/w200${selectedMovie.posterPath}`}
                                                alt={`${selectedMovie.title} poster`}
                                                loading="lazy"
                                            />
                                        {:else}
                                            <div class="no-poster">No poster available</div>
                                        {/if}
                                        <div class="movie-details">
                                            <h3>{selectedMovie.title}</h3>
                                            <button class="change-movie" on:click={() => selectedMovie = null}>
                                                Change Movie
                                            </button>
                                        </div>
                                    </div>

                                    <div class="review-input">
                                        <div class="rating-input">
                                            <label>Rating:</label>
                                            <div class="stars">
                                                {#each Array(5) as _, i}
                                                    <button
                                                        class="star-button {i < reviewRating ? 'filled' : ''}"
                                                        on:click={() => reviewRating = i + 1}
                                                    >
                                                        ★
                                                    </button>
                                                {/each}
                                            </div>
                                        </div>

                                        <textarea
                                            bind:value={reviewContent}
                                            placeholder="Write your review..."
                                            rows="4"
                                        ></textarea>

                                        <button 
                                            class="submit-review"
                                            on:click={submitReview}
                                            disabled={submittingReview || !reviewContent.trim()}
                                        >
                                            {submittingReview ? 'Submitting...' : 'Submit Review'}
                                        </button>
                                    </div>
                                </div>
                            {/if}
                        </div>
                    {/if}

                    {#if reviews.length === 0}
                        <p class="no-reviews">No reviews yet</p>
                    {:else}
                        <div class="reviews-list">
                            {#each reviews as review}
                                <div class="review-card">
                                    <div class="review-content-wrapper">
                                        {#if movieDetails.has(review.tmdbMovieId)}
                                            {@const movie = movieDetails.get(review.tmdbMovieId)}
                                            <div class="movie-poster">
                                                <img 
                                                    src={`https://image.tmdb.org/t/p/w200${movie?.posterPath}`}
                                                    alt={`${movie?.title} poster`}
                                                    loading="lazy"
                                                />
                                            </div>
                                        {/if}
                                        <div class="review-details">
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

    .profile-navigation {
        display: flex;
        background-color: #14181f;
        border-bottom: 1px solid #2c3440;
    }

    .nav-link {
        flex: 1;
        padding: 1rem;
        text-align: center;
        color: #9ab;
        text-decoration: none;
        font-weight: 500;
        transition: all 0.2s;
        border-bottom: 2px solid transparent;
    }

    .nav-link:hover {
        background-color: #2c3440;
        color: #fff;
    }

    .nav-link.active {
        color: #00e054;
        border-bottom-color: #00e054;
        background-color: #1a1f2b;
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

    .review-content-wrapper {
        display: flex;
        gap: 1.5rem;
    }

    .movie-poster {
        flex-shrink: 0;
        width: 100px;
        height: 150px;
        border-radius: 4px;
        overflow: hidden;
        background-color: #2c3440;
    }

    .movie-poster img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .review-details {
        flex: 1;
        min-width: 0;
    }

    .review-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
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

    .reviews-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .add-review-button {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .add-review-button:hover {
        background-color: #00c04b;
    }

    .review-form {
        background-color: #14181f;
        border-radius: 8px;
        padding: 1.5rem;
        margin-bottom: 2rem;
    }

    .movie-search {
        margin-bottom: 1rem;
    }

    .search-input {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;
    }

    .search-input input {
        flex: 1;
        padding: 0.75rem;
        border: 1px solid #2c3440;
        border-radius: 4px;
        background-color: #1a1f2b;
        color: #fff;
        font-size: 1rem;
    }

    .search-input input:focus {
        outline: none;
        border-color: #00e054;
    }

    .search-input button {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .search-input button:hover:not(:disabled) {
        background-color: #00c04b;
    }

    .search-input button:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }

    .search-error {
        color: #ff4444;
        margin-bottom: 1rem;
    }

    .search-results {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        max-height: 300px;
        overflow-y: auto;
    }

    .search-result-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 0.5rem;
        background-color: #1a1f2b;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .search-result-item:hover {
        background-color: #2c3440;
    }

    .search-result-item img {
        width: 46px;
        height: 69px;
        object-fit: cover;
        border-radius: 4px;
    }

    .no-poster-small {
        width: 46px;
        height: 69px;
        background-color: #2c3440;
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #9ab;
        font-size: 0.8rem;
    }

    .selected-movie {
        margin-bottom: 1.5rem;
    }

    .selected-movie-info {
        display: flex;
        gap: 1.5rem;
        margin-bottom: 1.5rem;
    }

    .selected-movie-info img {
        width: 100px;
        height: 150px;
        object-fit: cover;
        border-radius: 4px;
    }

    .movie-details {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .movie-details h3 {
        margin: 0;
        color: #fff;
    }

    .change-movie {
        background-color: transparent;
        color: #9ab;
        border: 1px solid #2c3440;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.2s;
    }

    .change-movie:hover {
        background-color: #2c3440;
        color: #fff;
    }

    .review-input {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .rating-input {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .rating-input label {
        color: #9ab;
    }

    .stars {
        display: flex;
        gap: 0.25rem;
    }

    .star-button {
        background: none;
        border: none;
        color: #2c3440;
        font-size: 1.5rem;
        cursor: pointer;
        padding: 0;
        transition: color 0.2s;
    }

    .star-button.filled {
        color: #00e054;
    }

    .star-button:hover {
        color: #00e054;
    }

    textarea {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #2c3440;
        border-radius: 4px;
        background-color: #1a1f2b;
        color: #fff;
        font-size: 1rem;
        resize: vertical;
    }

    textarea:focus {
        outline: none;
        border-color: #00e054;
    }

    .submit-review {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
        align-self: flex-end;
    }

    .submit-review:hover:not(:disabled) {
        background-color: #00c04b;
    }

    .submit-review:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }
</style> 