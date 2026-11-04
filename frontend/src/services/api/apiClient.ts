import type { ApiErrorPayload, ApiFieldErrors } from '../../types/api'
import { ApiError } from '../../types/api'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080').replace(/\/$/, '')

function parseFieldErrors(payload: unknown): ApiFieldErrors {
  if (!payload || typeof payload !== 'object') {
    return {}
  }

  const fieldErrors = (payload as { fieldErrors?: unknown }).fieldErrors
  if (!fieldErrors || typeof fieldErrors !== 'object') {
    return {}
  }

  return Object.fromEntries(
    Object.entries(fieldErrors as Record<string, unknown>).map(([key, value]) => [key, String(value)]),
  )
}

function parseApiError(status: number, payload: unknown): ApiError {
  const errorPayload = (payload ?? {}) as ApiErrorPayload
  const message = typeof errorPayload.message === 'string' && errorPayload.message.trim().length > 0
    ? errorPayload.message
    : typeof errorPayload.error === 'string' && errorPayload.error.trim().length > 0
      ? errorPayload.error
      : 'Request failed.'

  return new ApiError(status, message, parseFieldErrors(payload))
}

export function getStoredToken(): string | null {
  return window.localStorage.getItem('snapit-access-token')
}

export function clearStoredAuth(): void {
  window.localStorage.removeItem('snapit-access-token')
  window.localStorage.removeItem('snapit-auth-user')
  window.dispatchEvent(new CustomEvent('snapit:auth-expired'))
}

export type ApiRequestOptions = RequestInit & {
  json?: unknown
  formData?: FormData
  skipAuth?: boolean
}

export async function apiRequest<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers ?? {})
  const token = options.skipAuth ? null : getStoredToken()

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const hasFormData = Boolean(options.formData)
  let body: BodyInit | undefined = options.body as BodyInit | undefined

  if (options.json !== undefined && !hasFormData) {
    headers.set('Content-Type', 'application/json')
    body = JSON.stringify(options.json)
  }

  if (options.formData) {
    body = options.formData
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    body,
  })

  if (response.status === 204) {
    return undefined as T
  }

  const contentType = response.headers.get('content-type') ?? ''
  const payload = contentType.includes('application/json')
    ? await response.json().catch(() => null)
    : await response.text().catch(() => null)

  if (!response.ok) {
    const error = parseApiError(response.status, payload)
    if (response.status === 401) {
      clearStoredAuth()
    }
    throw error
  }

  if (contentType.includes('application/json') || contentType.includes('text/plain')) {
    return payload as T
  }

  return undefined as T
}

export async function apiDownload(path: string): Promise<Blob> {
  const token = getStoredToken()
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  })

  if (!response.ok) {
    const payload = await response.text().catch(() => null)
    throw parseApiError(response.status, payload)
  }

  return response.blob()
}

export { API_BASE_URL }
