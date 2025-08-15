// User types for the Timeforing application

export interface User {
  id: string;
  navn: string;
  mobil: string;
  epost: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  navn: string;
  mobil: string;
  epost: string;
}

export interface UpdateUserRequest {
  navn?: string;
  mobil?: string;
  epost?: string;
}

export interface UserFormData {
  navn: string;
  mobil: string;
  epost: string;
}

// API Response types
export interface ApiError {
  message: string;
  code: string;
  status: number;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

// Form validation error types
export interface ValidationError {
  field: string;
  message: string;
}

export interface FormErrors {
  navn?: string;
  mobil?: string;
  epost?: string;
  general?: string;
}
