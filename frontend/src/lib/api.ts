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

export const api = {
    register: async (data: RegisterUserRequest): Promise<UserDto> => {
        const response = await axios.post(`${API_URL}/users/register`, data);
        return response.data;
    }
}; 