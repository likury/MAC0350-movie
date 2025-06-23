import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export interface RegisterUserRequest {
    username: string;
    email: string;
    password: string;
}

export interface UserDto {
    id: number;
    username: string;
    email: string;
}

export interface MovieDto {
    id: number;
    tmdbId: number;
    title: string;
    overview: string;
    posterPath: string;
    releaseDate: string;
}

export interface ReviewDto {
    id: number;
    userId: number;
    movieId: number;
    tmdbMovieId: number;
    content: string;
    rating: number;
    createdAt: string;
}

export interface FollowDto {
    id: number;
    followerId: number;
    followedId: number;
}

export interface LikeDto {
    id: number;
    userId: number;
    reviewId: number;
    createdAt: string;
}

export interface SubmitLikeRequest {
    userId: number;
    reviewId: number;
}

export const api = {
    register: async (data: RegisterUserRequest): Promise<UserDto> => {
        const response = await axios.post(`${API_URL}/users/register`, data);
        return response.data;
    },

    getUserByUsername: async (username: string): Promise<UserDto> => {
        const response = await axios.get(`${API_URL}/users/username/${username}`);
        return response.data;
    },

    getUserById: async (id: number): Promise<UserDto> => {
        const response = await axios.get(`${API_URL}/users/${id}`);
        return response.data;
    },

    getUserReviews: async (userId: number): Promise<ReviewDto[]> => {
        const response = await axios.get(`${API_URL}/reviews/user/${userId}`);
        return response.data;
    },

    getMovieDetails: async (tmdbId: number): Promise<MovieDto> => {
        const response = await axios.get(`${API_URL}/movies/tmdb/${tmdbId}`);
        return response.data;
    },

    getPopularMovies: async (page: number = 1): Promise<MovieDto[]> => {
        const response = await axios.get(`${API_URL}/movies/popular?page=${page}`);
        return response.data;
    },

    searchMovies: async (query: string, page: number = 1): Promise<MovieDto[]> => {
        const response = await axios.get(`${API_URL}/movies/search?query=${encodeURIComponent(query)}&page=${page}`);
        return response.data;
    },

    submitReview: async (userId: number, tmdbId: number, content: string, rating: number): Promise<ReviewDto> => {
        const response = await axios.post(`${API_URL}/reviews/user/${userId}/movie/${tmdbId}`, {
            content,
            rating
        });
        return response.data;
    },

    // Follow-related functions
    followUser: async (followerId: number, followedId: number): Promise<FollowDto> => {
        const response = await axios.post(`${API_URL}/follows/${followerId}/follow/${followedId}`);
        return response.data;
    },

    getFollowers: async (userId: number): Promise<FollowDto[]> => {
        const response = await axios.get(`${API_URL}/follows/${userId}/followers`);
        return response.data;
    },

    getFollowing: async (userId: number): Promise<FollowDto[]> => {
        const response = await axios.get(`${API_URL}/follows/${userId}/following`);
        return response.data;
    },

    // Note: Using getUserByUsername for exact username search
    searchUsers: async (query: string): Promise<UserDto[]> => {
        if (!query.trim()) {
            return [];
        }
        
        try {
            // Use the existing getUserByUsername endpoint for exact username search
            const response = await axios.get(`${API_URL}/users/username/${encodeURIComponent(query.trim())}`);
            return [response.data]; // Return as array since we found one user
        } catch (e) {
            // User not found or other error - return empty array
            return [];
        }
    },

    // Like-related functions
    likeReview: async (userId: number, reviewId: number): Promise<LikeDto> => {
        const response = await axios.post(`${API_URL}/likes`, {
            userId,
            reviewId
        });
        return response.data;
    },

    getLikesForReview: async (reviewId: number): Promise<LikeDto[]> => {
        const response = await axios.get(`${API_URL}/likes/review/${reviewId}`);
        return response.data;
    }
}; 