export type LoginRequest = {
  email: string
  password: string
}

export type RegisterRequest = {
  username: string
  email: string
  password: string
}

export type AuthResponse = {
  accessToken: string
  tokenType: string
  id: number
  username: string
  email: string
}

export type UserResponse = {
  id: number
  username: string
  email: string
  role?: string
  createdAt?: string
}

export type AuthUser = {
  id: number
  username: string
  email: string
}
