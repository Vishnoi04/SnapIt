import type { Collection } from '../types/collection'
import type { UserProfile } from '../types/user'
import { starterCollections } from './mockItems'

const COLLECTIONS_KEY = 'snapit-collections'
const PROFILE_KEY = 'snapit-profile'
const PREFERENCES_KEY = 'snapit-preferences'

export type UserPreferences = {
  weeklyDigest: boolean
  productUpdates: boolean
  privateByDefault: boolean
}

export const defaultPreferences: UserPreferences = {
  weeklyDigest: false,
  productUpdates: true,
  privateByDefault: true,
}

export const defaultProfile: UserProfile = {
  name: 'Vicky',
  username: 'vicky',
  email: 'vicky@example.com',
  bio: 'Collecting useful ideas and making room for the next one.',
}

export const initialCollections: Collection[] = starterCollections.map((name, index) => ({
  id: `collection-${index + 1}`,
  name,
  description: [
    'Places, materials, and visual details worth returning to.',
    'Working thoughts, references, and notes for the day.',
    'Good reads to come back to when there is a little more time.',
  ][index],
  createdAt: new Date(Date.now() - (index + 1) * 86400000).toISOString(),
}))

function readStorage<T>(key: string, fallback: T, isValid: (value: unknown) => value is T): T {
  const saved = window.localStorage.getItem(key)
  if (!saved) return fallback
  try {
    const parsed: unknown = JSON.parse(saved)
    return isValid(parsed) ? parsed : fallback
  } catch {
    return fallback
  }
}

const isCollectionList = (value: unknown): value is Collection[] => Array.isArray(value)
  && value.every((entry) => Boolean(entry)
    && typeof entry === 'object'
    && typeof entry.id === 'string'
    && typeof entry.name === 'string'
    && typeof entry.description === 'string'
    && typeof entry.createdAt === 'string')

const isUserProfile = (value: unknown): value is UserProfile => Boolean(value)
  && typeof value === 'object'
  && typeof (value as UserProfile).name === 'string'
  && typeof (value as UserProfile).username === 'string'
  && typeof (value as UserProfile).email === 'string'
  && typeof (value as UserProfile).bio === 'string'

const isPreferences = (value: unknown): value is UserPreferences => Boolean(value)
  && typeof value === 'object'
  && typeof (value as UserPreferences).weeklyDigest === 'boolean'
  && typeof (value as UserPreferences).productUpdates === 'boolean'
  && typeof (value as UserPreferences).privateByDefault === 'boolean'

export const loadCollections = () => readStorage(COLLECTIONS_KEY, initialCollections, isCollectionList)
export const loadProfile = () => readStorage(PROFILE_KEY, defaultProfile, isUserProfile)
export const loadPreferences = () => readStorage(PREFERENCES_KEY, defaultPreferences, isPreferences)

export function saveCollections(collections: Collection[]): void {
  window.localStorage.setItem(COLLECTIONS_KEY, JSON.stringify(collections))
}

export function saveProfile(profile: UserProfile): void {
  window.localStorage.setItem(PROFILE_KEY, JSON.stringify(profile))
}

export function savePreferences(preferences: UserPreferences): void {
  window.localStorage.setItem(PREFERENCES_KEY, JSON.stringify(preferences))
}
