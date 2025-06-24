<script lang="ts">
    import { api } from '$lib/api';
    import { page } from '$app/stores';
    import { onMount } from 'svelte';
    import type { UserDto, ReviewDto, FollowDto, LikeDto } from '$lib/api';

    let user: UserDto | null = null;
    let feedReviews: (ReviewDto & { reviewerUsername?: string; movieTitle?: string; moviePosterPath?: string; likeCount?: number; isLiked?: boolean })[] = [];
    let loading = true;
    let error = '';
    let likingReview = false;

    // Cache for movie details and user details
    let movieDetails: Map<number, { title: string; posterPath: string }> = new Map();
    let reviewerDetails: Map<number, UserDto> = new Map();

    async function loadMovieDetails(tmdbId: number) {
        if (movieDetails.has(tmdbId)) return;
        
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

    async function loadReviewerDetails(userId: number) {
        if (reviewerDetails.has(userId)) return;
        
        try {
            const reviewer = await api.getUserById(userId);
            reviewerDetails.set(userId, reviewer);
        } catch (e) {
            console.error(`Failed to load reviewer details for user ID ${userId}:`, e);
        }
    }

    async function loadReviewLikes(reviewId: number) {
        try {
            const likes = await api.getLikesForReview(reviewId);
            
            // Update the specific review in the feedReviews array
            feedReviews = feedReviews.map(review => {
                if (review.id === reviewId) {
                    return {
                        ...review,
                        likeCount: likes.length,
                        isLiked: user ? likes.some(like => like.userId === user!.id) : false
                    };
                }
                return review;
            });
        } catch (e) {
            console.error(`Failed to load likes for review ${reviewId}:`, e);
        }
    }

    async function loadFeedData() {
        if (!user) return;
        
        try {
            // Get users that current user is following
            const following = await api.getFollowing(user.id);
            
            if (following.length === 0) {
                feedReviews = [];
                return;
            }

            // Get reviews from all followed users
            const reviewPromises = following.map(follow => 
                api.getUserReviews(follow.followedId)
            );
            
            const allReviews = await Promise.all(reviewPromises);
            const flattenedReviews = allReviews.flat();
            
            // Sort by creation date (newest first)
            feedReviews = flattenedReviews.sort((a, b) => 
                new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
            );

            // Load movie details and reviewer details
            const uniqueTmdbIds = [...new Set(feedReviews.map(review => review.tmdbMovieId))];
            const uniqueUserIds = [...new Set(feedReviews.map(review => review.userId))];
            
            await Promise.all([
                ...uniqueTmdbIds.map(loadMovieDetails),
                ...uniqueUserIds.map(loadReviewerDetails)
            ]);

            // Enhance reviews with movie and reviewer info first
            feedReviews = feedReviews.map(review => ({
                ...review,
                movieTitle: movieDetails.get(review.tmdbMovieId)?.title,
                moviePosterPath: movieDetails.get(review.tmdbMovieId)?.posterPath,
                reviewerUsername: reviewerDetails.get(review.userId)?.username,
                likeCount: 0,
                isLiked: false
            }));

            // Load likes for all reviews (this will update the feedReviews array)
            await Promise.all(feedReviews.map(review => loadReviewLikes(review.id)));

        } catch (e) {
            console.error('Error loading feed data:', e);
            error = 'Failed to load feed';
        }
    }

    async function toggleLikeReview(reviewId: number) {
        if (!user || likingReview) return;
        
        try {
            likingReview = true;
            
            // Find the current review
            const currentReview = feedReviews.find(r => r.id === reviewId);
            if (!currentReview) return;
            
            if (currentReview.isLiked) {
                // Unlike functionality would need a backend endpoint - for now just prevent re-liking
                return;
            } else {
                // Optimistically update UI first
                feedReviews = feedReviews.map(review => {
                    if (review.id === reviewId) {
                        return {
                            ...review,
                            isLiked: true,
                            likeCount: (review.likeCount || 0) + 1
                        };
                    }
                    return review;
                });
                
                // Then make the API call
                await api.likeReview(user.id, reviewId);
                
                // Refresh likes for this review to get accurate data
                await loadReviewLikes(reviewId);
            }
        } catch (e) {
            console.error('Error toggling like:', e);
            // Revert optimistic update on error
            feedReviews = feedReviews.map(review => {
                if (review.id === reviewId) {
                    return {
                        ...review,
                        isLiked: false,
                        likeCount: Math.max((review.likeCount || 1) - 1, 0)
                    };
                }
                return review;
            });
            await loadReviewLikes(reviewId); // Reload to get correct state
        } finally {
            likingReview = false;
        }
    }

    function getLikeCount(reviewId: number): number {
        const review = feedReviews.find(r => r.id === reviewId);
        return review?.likeCount || 0;
    }

    function isLikedByUser(reviewId: number): boolean {
        const review = feedReviews.find(r => r.id === reviewId);
        return review?.isLiked || false;
    }

    onMount(async () => {
        try {
            const username = $page.params.username;
            const userResponse = await api.getUserByUsername(username);
            user = userResponse;
            
            if (user) {
                await loadFeedData();
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
        <div class="loading">Loading your feed...</div>
    {:else if error}
        <div class="error-container">
            <h1>Oops! 😕</h1>
            <p class="error-message">{error}</p>
            <a href="/register" class="button">Create an Account</a>
        </div>
    {:else if user}
        <div class="feed-container">
            <div class="feed-header">
                <h1>{user.username}'s Feed</h1>
                <p class="feed-subtitle">Reviews from people you follow</p>
            </div>

            <div class="profile-navigation">
                <a href="/user/{user.username}" class="nav-link">Profile</a>
                <a href="/user/{user.username}/social" class="nav-link">Social</a>
                <a href="/user/{user.username}/feed" class="nav-link active">Feed</a>
            </div>
            
            <div class="feed-content">
                {#if feedReviews.length === 0}
                    <div class="no-reviews">
                        <h2>Your feed is empty</h2>
                        <p>Start following other users to see their reviews here!</p>
                        <a href="/user/{user.username}/social" class="button">Find Users to Follow</a>
                    </div>
                {:else}
                    <div class="reviews-list">
                        {#each feedReviews as review}
                            <div class="review-card">
                                <div class="review-header">
                                    <div class="reviewer-info">
                                        <span class="reviewer-name">
                                            <a href="/user/{review.reviewerUsername}">{review.reviewerUsername}</a>
                                        </span>
                                        <span class="review-date">{new Date(review.createdAt).toLocaleDateString()}</span>
                                    </div>
                                    <div class="movie-info">
                                        <span class="movie-title">{review.movieTitle || 'Loading...'}</span>
                                    </div>
                                </div>

                                <div class="review-content-wrapper">
                                    {#if review.moviePosterPath}
                                        <div class="movie-poster">
                                            <img 
                                                src={`https://image.tmdb.org/t/p/w200${review.moviePosterPath}`}
                                                alt={`${review.movieTitle} poster`}
                                                loading="lazy"
                                            />
                                        </div>
                                    {/if}
                                    
                                    <div class="review-details">
                                        <div class="rating">
                                            {#each Array(5) as _, i}
                                                <span class="star {i < review.rating ? 'filled' : ''}">★</span>
                                            {/each}
                                        </div>

                                        <p class="review-content">{review.content}</p>

                                        <div class="review-actions">
                                            <button 
                                                class="like-button {isLikedByUser(review.id) ? 'liked' : ''}"
                                                on:click={() => toggleLikeReview(review.id)}
                                                disabled={likingReview}
                                            >
                                                <span class="like-icon">♥</span>
                                                <span class="like-count">{getLikeCount(review.id)}</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        {/each}
                    </div>
                {/if}
            </div>
        </div>
    {/if}
</div>

<style>
    .container {
        min-height: 100vh;
        background-color: #14181f;
        color: #fff;
        padding: 1rem;
    }

    .loading {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 50vh;
        color: #00e054;
        font-size: 1.2rem;
    }

    .error-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 50vh;
        background-color: #1a1f2b;
        padding: 2rem;
        border-radius: 8px;
        text-align: center;
        max-width: 400px;
        margin: 0 auto;
    }

    .error-message {
        color: #ff4444;
        margin: 1rem 0;
    }

    .feed-container {
        max-width: 800px;
        margin: 0 auto;
    }

    .feed-header {
        background-color: #1a1f2b;
        padding: 2rem;
        border-radius: 8px;
        text-align: center;
        margin-bottom: 2rem;
    }

    .feed-header h1 {
        color: #00e054;
        margin: 0 0 0.5rem 0;
        font-size: 2rem;
    }

    .feed-subtitle {
        color: #9ab;
        margin: 0;
        font-size: 1.1rem;
    }

    .profile-navigation {
        display: flex;
        background-color: #14181f;
        border-bottom: 1px solid #2c3440;
        margin-bottom: 2rem;
        border-radius: 8px 8px 0 0;
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

    .feed-content {
        background-color: #1a1f2b;
        border-radius: 8px;
        overflow: hidden;
    }

    .no-reviews {
        padding: 3rem 2rem;
        text-align: center;
    }

    .no-reviews h2 {
        color: #fff;
        margin-bottom: 1rem;
    }

    .no-reviews p {
        color: #9ab;
        margin-bottom: 2rem;
    }

    .reviews-list {
        display: flex;
        flex-direction: column;
        gap: 2rem;
        padding: 2rem;
    }

    .review-card {
        background-color: #14181f;
        border-radius: 8px;
        padding: 1.5rem;
        border: 1px solid #2c3440;
    }

    .review-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1rem;
        flex-wrap: wrap;
        gap: 1rem;
    }

    .reviewer-info {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .reviewer-name a {
        color: #00e054;
        text-decoration: none;
        font-weight: 500;
        font-size: 1.1rem;
    }

    .reviewer-name a:hover {
        text-decoration: underline;
    }

    .review-date {
        color: #9ab;
        font-size: 0.9rem;
    }

    .movie-info {
        text-align: right;
    }

    .movie-title {
        color: #fff;
        font-size: 1.1rem;
        font-weight: 500;
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

    .rating {
        display: flex;
        gap: 0.25rem;
        margin-bottom: 1rem;
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
        line-height: 1.6;
        margin: 0 0 1rem 0;
    }

    .review-actions {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .like-button {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: none;
        border: 1px solid #2c3440;
        color: #9ab;
        padding: 0.5rem 1rem;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.2s;
        font-size: 0.9rem;
    }

    .like-button:hover:not(:disabled) {
        background-color: #2c3440;
        color: #fff;
    }

    .like-button:disabled {
        cursor: not-allowed;
        opacity: 0.6;
    }

    .like-button.liked {
        background-color: #ff4444;
        border-color: #ff4444;
        color: #fff;
    }

    .like-button.liked:hover:not(:disabled) {
        background-color: #ff6666;
        border-color: #ff6666;
    }

    .like-icon {
        font-size: 1.1rem;
    }

    .like-count {
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
        transition: background-color 0.2s;
    }

    .button:hover {
        background-color: #00c04b;
    }

    @media (max-width: 600px) {
        .review-content-wrapper {
            flex-direction: column;
            gap: 1rem;
        }

        .movie-poster {
            width: 80px;
            height: 120px;
            align-self: flex-start;
        }

        .review-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .movie-info {
            text-align: left;
        }
    }
</style> 