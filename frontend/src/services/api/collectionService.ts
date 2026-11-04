import type { PageResponse } from '../../types/api'
import { apiRequest } from './apiClient'

export type CollectionApiType = {
  id: number
  name: string
  description: string
  createdAt: string
}

export async function fetchCollections(page = 0, size = 20): Promise<PageResponse<CollectionApiType>> {
  return apiRequest<PageResponse<CollectionApiType>>(`/api/collections?page=${page}&size=${size}`)
}

export async function createCollection(payload: { name: string; description?: string }): Promise<CollectionApiType> {
  return apiRequest<CollectionApiType>('/api/collections', {
    method: 'POST',
    json: payload,
  })
}

export async function updateCollection(id: number, payload: { name: string; description?: string }): Promise<CollectionApiType> {
  return apiRequest<CollectionApiType>(`/api/collections/${id}`, {
    method: 'PUT',
    json: payload,
  })
}

export async function deleteCollection(id: number): Promise<void> {
  await apiRequest<void>(`/api/collections/${id}`, { method: 'DELETE' })
}
