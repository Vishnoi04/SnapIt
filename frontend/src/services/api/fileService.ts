import { apiDownload, apiRequest } from './apiClient'

export async function downloadSavedItemFile(itemId: number): Promise<Blob> {
  return apiDownload(`/api/saved-items/${itemId}/file`)
}

export async function deleteSavedItemFile(itemId: number): Promise<void> {
  await apiRequest<void>(`/api/saved-items/${itemId}/file`, { method: 'DELETE' })
}
