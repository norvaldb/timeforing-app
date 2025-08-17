import axios, { AxiosInstance, AxiosError } from 'axios';
import { ApiError } from '@/types/user';

// Create axios instance with base configuration
const apiClient: AxiosInstance = axios.create({
  baseURL: (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:3001/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token if available
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const apiError: ApiError = {
      message: error.message || 'Noe gikk galt',
      code: error.code || 'UNKNOWN_ERROR',
      status: error.response?.status || 500,
    };

    // Handle specific error cases
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 400:
          apiError.message = 'Ugyldig forespørsel';
          apiError.code = 'BAD_REQUEST';
          break;
        case 401:
          apiError.message = 'Ikke autorisert';
          apiError.code = 'UNAUTHORIZED';
          break;
        case 409:
          apiError.message = 'Epost addressen er allerede registrert';
          apiError.code = 'DUPLICATE_EMAIL';
          break;
        case 422:
          apiError.message = 'Ugyldig data';
          apiError.code = 'VALIDATION_ERROR';
          break;
        case 500:
          apiError.message = 'Server feil';
          apiError.code = 'SERVER_ERROR';
          break;
        default:
          apiError.message = (data as any)?.message || 'Noe gikk galt, prøv igjen';
      }
    } else if (error.request) {
      apiError.message = 'Kunne ikke kontakte server';
      apiError.code = 'NETWORK_ERROR';
    }

    return Promise.reject(apiError);
  }
);

export { apiClient };
export type { ApiError };
