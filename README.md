# MovieDB - Movie Review Platform - MAC0350 project

A movie review platform similar to Letterboxd, built with Spring Boot (Kotlin) and SvelteKit.

## Frontend Routes

### User Routes
- `/register` - User registration page
- `/user/[username]` - User profile page showing user information and their reviews

## Backend API Endpoints

### User Controller (`/api/users`)
- `POST /api/users/register` - Register a new user
  - Request Body: `{ username: string, email: string, password: string }`
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username

### Movie Controller (`/api/movies`)
- `POST /api/movies` - Save a new movie
  - Request Body: `{ tmdbId: number, title: string, posterPath: string }`
- `GET /api/movies/tmdb/{tmdbId}` - Get movie details by TMDB ID
- `GET /api/movies/search` - Search movies
  - Query Parameters: 
    - `query: string` - Search term
    - `page: number` (default: 1) - Page number
- `GET /api/movies/popular` - Get popular movies
  - Query Parameters:
    - `page: number` (default: 1) - Page number

### Review Controller (`/api/reviews`)
- `POST /api/reviews/user/{userId}/movie/{tmdbId}` - Submit a review
  - Request Body: `{ content: string, rating: number }`
- `GET /api/reviews/movie/{tmdbId}` - Get reviews for a movie
- `GET /api/reviews/user/{userId}` - Get reviews by a user

### Like Controller (`/api/likes`)
- `POST /api/likes/user/{userId}/review/{reviewId}` - Like a review
- `DELETE /api/likes/user/{userId}/review/{reviewId}` - Unlike a review
- `GET /api/likes/review/{reviewId}` - Get likes for a review
- `GET /api/likes/user/{userId}` - Get likes by a user

### Follow Controller (`/api/follows`)
- `POST /api/follows/follower/{followerId}/following/{followingId}` - Follow a user
- `DELETE /api/follows/follower/{followerId}/following/{followingId}` - Unfollow a user
- `GET /api/follows/following/{userId}` - Get users that a user follows
- `GET /api/follows/followers/{userId}` - Get users that follow a user

## Technologies Used

### Frontend
- SvelteKit
- TypeScript
- Axios for API calls

### Backend
- Spring Boot
- Kotlin
- JPA/Hibernate
- PostgreSQL

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL

### Running the Backend
1. Navigate to the backend directory
2. Run the Spring Boot application
3. The server will start on port 8080

### Running the Frontend
1. Navigate to the frontend directory
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`
4. The application will be available at http://localhost:5173

## Features
- User registration and profiles
- Movie reviews and ratings
- Like and follow functionality
- Movie search and discovery
- Popular movies listing
