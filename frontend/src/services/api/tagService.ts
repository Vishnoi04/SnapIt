import type { PageResponse } from '../../types/api'
import { apiRequest } from './apiClient'

export type TagApiType = {
  id: number
  name: string
  createdAt: string
}

export async function fetchTags(page = 0, size = 20): Promise<PageResponse<TagApiType>> {
  return apiRequest<PageResponse<TagApiType>>(`/api/tags?page=${page}&size=${size}`)
}

export async function createTag(payload: { name: string }): Promise<TagApiType> {
  return apiRequest<TagApiType>('/api/tags', {
    method: 'POST',
    json: payload,
  })
}

export async function updateTag(id: number, payload: { name: string }): Promise<TagApiType> {
  return apiRequest<TagApiType>(`/api/tags/${id}`, {
    method: 'PUT',
    json: payload,
  })
}

export async function deleteTag(id: number): Promise<void> {
  await apiRequest<void>(`/api/tags/${id}`, { method: 'DELETE' })
}
