export type SavedItemType = 'website' | 'image' | 'pdf' | 'note' | 'code'

export type SavedItem = {
  id: string
  type: SavedItemType
  title: string
  description: string
  content: string
  url: string
  tags: string[]
  collection: string
  favorite: boolean
  createdAt: string
  updatedAt: string
  fileName: string
  language: string
}

export const savedItemTypes: SavedItemType[] = ['website', 'image', 'pdf', 'note', 'code']

export const savedItemTypeLabels: Record<SavedItemType, string> = {
  website: 'Website',
  image: 'Image',
  pdf: 'PDF',
  note: 'Text note',
  code: 'Code snippet',
}

export function isSavedItemType(value: string): value is SavedItemType {
  return savedItemTypes.some((type) => type === value)
}

export function isSavedItem(value: unknown): value is SavedItem {
  if (!value || typeof value !== 'object') return false

  const item = value as Record<string, unknown>
  return typeof item.id === 'string'
    && typeof item.type === 'string'
    && isSavedItemType(item.type)
    && typeof item.title === 'string'
    && typeof item.description === 'string'
    && typeof item.content === 'string'
    && typeof item.url === 'string'
    && Array.isArray(item.tags)
    && item.tags.every((tag) => typeof tag === 'string')
    && typeof item.collection === 'string'
    && typeof item.favorite === 'boolean'
    && typeof item.createdAt === 'string'
    && typeof item.updatedAt === 'string'
    && typeof item.fileName === 'string'
    && typeof item.language === 'string'
}
