import type { LoginRequest, RegisterRequest, AuthResponse, UserResponse } from '../../types/auth'
import { apiRequest } from './apiClient'

export async function loginUser(payload: LoginRequest): Promise<AuthResponse> {
  return apiRequest<AuthResponse>('/api/auth/login', {
    method: 'POST',
    json: payload,
    skipAuth: true,
  })
}

export async function registerUser(payload: RegisterRequest): Promise<UserResponse> {
  return apiRequest<UserResponse>('/api/auth/register', {
    method: 'POST',
    json: payload,
    skipAuth: true,
  })
}

export function logoutUser(): void {
  window.localStorage.removeItem('snapit-access-token')
  window.localStorage.removeItem('snapit-auth-user')
}
