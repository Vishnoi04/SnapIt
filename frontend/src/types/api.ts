export type ApiFieldErrors = Record<string, string>

export type ApiErrorPayload = {
  message?: string
  fieldErrors?: ApiFieldErrors
  error?: string
}

export class ApiError extends Error {
  status: number
  fieldErrors: ApiFieldErrors

  constructor(status: number, message: string, fieldErrors: ApiFieldErrors = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

export type PageResponse<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}
