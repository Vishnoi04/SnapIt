import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { loginUser, logoutUser, registerUser } from '../services/api/authService'
import type { AuthUser, LoginRequest, RegisterRequest, UserResponse } from '../types/auth'

const STORAGE_KEY = 'snapit-auth-user'
const TOKEN_STORAGE_KEY = 'snapit-access-token'

type AuthContextValue = {
  user: AuthUser | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (payload: LoginRequest) => Promise<void>
  signup: (payload: RegisterRequest) => Promise<UserResponse>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function readStoredAuth(): { user: AuthUser | null; token: string | null } {
  try {
    const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)
    const rawUser = window.localStorage.getItem(STORAGE_KEY)
    if (!token || !rawUser) {
      return { user: null, token: null }
    }

    const parsedUser = JSON.parse(rawUser) as Partial<AuthUser>
    if (!parsedUser || typeof parsedUser.id !== 'number' || typeof parsedUser.username !== 'string' || typeof parsedUser.email !== 'string') {
      return { user: null, token: null }
    }

    return {
      user: {
        id: parsedUser.id,
        username: parsedUser.username,
        email: parsedUser.email,
      },
      token,
    }
  } catch {
    return { user: null, token: null }
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const stored = readStoredAuth()
    setUser(stored.user)
    setToken(stored.token)
    setIsLoading(false)
  }, [])

  useEffect(() => {
    const handleExpired = () => {
      setUser(null)
      setToken(null)
      logoutUser()
    }

    window.addEventListener('snapit:auth-expired', handleExpired)
    return () => window.removeEventListener('snapit:auth-expired', handleExpired)
  }, [])

  useEffect(() => {
    if (token && user) {
      window.localStorage.setItem(TOKEN_STORAGE_KEY, token)
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
      return
    }

    if (!token) {
      window.localStorage.removeItem(TOKEN_STORAGE_KEY)
      window.localStorage.removeItem(STORAGE_KEY)
    }
  }, [token, user])

  async function login(payload: LoginRequest): Promise<void> {
    const response = await loginUser(payload)
    const nextUser: AuthUser = {
      id: response.id,
      username: response.username,
      email: response.email,
    }

    setToken(response.accessToken)
    setUser(nextUser)
  }

  async function signup(payload: RegisterRequest): Promise<UserResponse> {
    return registerUser(payload)
  }

  function logout(): void {
    setUser(null)
    setToken(null)
    logoutUser()
  }

  const value = useMemo<AuthContextValue>(() => ({
    user,
    token,
    isAuthenticated: Boolean(token && user),
    isLoading,
    login,
    signup,
    logout,
  }), [token, user, isLoading])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
