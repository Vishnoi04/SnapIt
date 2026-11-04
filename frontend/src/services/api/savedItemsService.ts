import type { PageResponse } from '../../types/api'
import { apiDownload, apiRequest } from './apiClient'

export type SavedItemApiType = {
  id: number
  title: string
  sourceUrl: string | null
  contentType: 'WEBPAGE' | 'IMGAGE' | 'TEXT' | 'PDF'
  description: string | null
  extractedText: string | null
  processingStatus?: string
  favorite: boolean
  language: string | null
  createdAt: string
  updatedAt: string
}

export type SavedItemCreateRequest = {
  title: string
  sourceUrl?: string | null
  contentType: 'WEBPAGE' | 'IMGAGE' | 'TEXT' | 'PDF'
  description?: string | null
  extractedText?: string | null
  language?: string | null
}

export type SavedItemFavoriteRequest = {
  favorite: boolean
}

export async function fetchSavedItems(page = 0, size = 10): Promise<PageResponse<SavedItemApiType>> {
  return apiRequest<PageResponse<SavedItemApiType>>(`/api/saved-items?page=${page}&size=${size}`)
}

export async function fetchFavorites(page = 0, size = 10): Promise<PageResponse<SavedItemApiType>> {
  return apiRequest<PageResponse<SavedItemApiType>>(`/api/saved-items/favorites?page=${page}&size=${size}`)
}

export async function fetchSavedItemById(id: number): Promise<SavedItemApiType> {
  return apiRequest<SavedItemApiType>(`/api/saved-items/${id}`)
}

export async function createSavedItem(payload: SavedItemCreateRequest): Promise<SavedItemApiType> {
  const result = await apiRequest<string>('/api/saved-items', {
    method: 'POST',
    json: payload,
  })
  const id = result.match(/\bID\s*:\s*(\d+)\b/i)?.[1]
  if (!id) {
    throw new Error('The saved item was created, but the server did not return its ID.')
  }
  return fetchSavedItemById(Number(id))
}

export async function updateSavedItem(id: number, payload: SavedItemCreateRequest): Promise<SavedItemApiType> {
  return apiRequest<SavedItemApiType>(`/api/saved-items/${id}`, {
    method: 'PUT',
    json: payload,
  })
}

export async function deleteSavedItem(id: number): Promise<void> {
  await apiRequest<void>(`/api/saved-items/${id}`, {
    method: 'DELETE',
  })
}

export async function setFavorite(id: number, value: boolean): Promise<SavedItemApiType> {
  return apiRequest<SavedItemApiType>(`/api/saved-items/${id}/favorite`, {
    method: 'PUT',
    json: { favorite: value },
  })
}

export async function searchSavedItems(keyword?: string, contentType?: string, favorite?: boolean, page = 0, size = 10): Promise<PageResponse<SavedItemApiType>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  if (keyword) params.set('keyword', keyword)
  if (contentType) params.set('contentType', contentType)
  if (favorite !== undefined) params.set('favorite', String(favorite))
  return apiRequest<PageResponse<SavedItemApiType>>(`/api/saved-items/search?${params.toString()}`)
}

export async function createWebpage(url: string): Promise<SavedItemApiType> {
  return apiRequest<SavedItemApiType>('/api/webpages', {
    method: 'POST',
    json: { url },
  })
}

export async function createTextSnippet(payload: { title: string; content: string; description?: string; language?: string }): Promise<SavedItemApiType> {
  return apiRequest<SavedItemApiType>('/api/snippets', {
    method: 'POST',
    json: payload,
  })
}

export async function uploadImage(file: File): Promise<SavedItemApiType> {
  const formData = new FormData()
  formData.append('file', file)
  return apiRequest<SavedItemApiType>('/api/images', {
    method: 'POST',
    formData,
  })
}

export async function uploadPdf(file: File): Promise<SavedItemApiType> {
  const formData = new FormData()
  formData.append('file', file)
  return apiRequest<SavedItemApiType>('/api/pdfs', {
    method: 'POST',
    formData,
  })
}

export async function fetchSavedItemFile(id: number): Promise<Blob> {
  return apiDownload(`/api/saved-items/${id}/file`)
}

export async function deleteSavedItemFile(id: number): Promise<void> {
  await apiRequest<void>(`/api/saved-items/${id}/file`, { method: 'DELETE' })
}
