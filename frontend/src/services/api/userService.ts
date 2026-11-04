import { apiRequest } from './apiClient'

export type PublicProfileResponse = {
  username: string
  memberSince?: string
}

export async function fetchPublicProfile(username: string): Promise<PublicProfileResponse> {
  return apiRequest<PublicProfileResponse>(`/api/profiles/${encodeURIComponent(username)}`)
}
