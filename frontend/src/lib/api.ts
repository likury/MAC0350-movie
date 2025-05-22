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

export const api = {
    register: async (data: RegisterUserRequest): Promise<UserDto> => {
        const response = await axios.post(`${API_URL}/users/register`, data);
        return response.data;
    },

    getUserByUsername: async (username: string): Promise<UserDto> => {
        const response = await axios.get(`${API_URL}/users/username/${username}`);
        return response.data;
    },

    getUserReviews: async (userId: number): Promise<ReviewDto[]> => {
        const response = await axios.get(`${API_URL}/reviews/user/${userId}`);
        return response.data;
    },

    getMovieDetails: async (tmdbId: number): Promise<MovieDto> => {
        const response = await axios.get(`${API_URL}/movies/tmdb/${tmdbId}`);
        return response.data;
    }
}; 