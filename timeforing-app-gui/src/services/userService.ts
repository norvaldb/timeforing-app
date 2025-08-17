import { apiClient } from './apiClient';
import { User, CreateUserRequest, UpdateUserRequest } from '@/types/user';

export class UserService {
  private baseUrl = '/users';

  /**
   * Register a new user
   */
  async register(userData: CreateUserRequest): Promise<User> {
    const response = await apiClient.post(`${this.baseUrl}/register`, userData);
    return response.data;
  }

  /**
   * Get current user profile
   */
  async getProfile(): Promise<User> {
    const response = await apiClient.get(`${this.baseUrl}/profile`);
    return response.data;
  }

  /**
   * Update user profile
   */
  async updateProfile(userData: UpdateUserRequest): Promise<User> {
    const response = await apiClient.put(`${this.baseUrl}/profile`, userData);
    return response.data;
  }

  /**
   * Get user by ID (for admin purposes)
   */
  async getById(id: string): Promise<User> {
    const response = await apiClient.get(`${this.baseUrl}/${id}`);
    return response.data;
  }

  /**
   * Delete user account
   */
  async deleteAccount(): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/profile`);
  }

  /**
   * Check if email is available
   */
  async checkEmailAvailability(email: string): Promise<boolean> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/check-email`, {
        params: { email }
      });
      return response.data.available;
    } catch (error) {
      return false;
    }
  }
}

// Export singleton instance
export const userService = new UserService();
