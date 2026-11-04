import { isSavedItem, type SavedItem } from '../types/savedItem'

const now = Date.now()
const daysAgo = (days: number) => new Date(now - days * 24 * 60 * 60 * 1000).toISOString()

export const starterCollections = ['Design ideas', 'Work notes', 'Reading list']

export const starterItems: SavedItem[] = [
  {
    id: 'item-quiet-spaces',
    type: 'image',
    title: 'Quiet spaces, considered',
    description: 'A reference for warm, thoughtful interiors and natural materials.',
    content: '',
    url: '',
    tags: ['interiors', 'inspiration'],
    collection: 'Design ideas',
    favorite: true,
    createdAt: daysAgo(1),
    updatedAt: daysAgo(1),
    fileName: 'quiet-spaces.jpg',
    language: '',
  },
  {
    id: 'item-field-notes',
    type: 'website',
    title: 'On making with intention',
    description: 'A short essay on building a slower, more considered creative practice.',
    content: 'Small, meaningful constraints can help make room for more intentional work.',
    url: 'https://fieldnotes.design/journal/making-with-intention',
    tags: ['design', 'reading'],
    collection: 'Reading list',
    favorite: false,
    createdAt: daysAgo(2),
    updatedAt: daysAgo(2),
    fileName: '',
    language: '',
  },
  {
    id: 'item-focus-note',
    type: 'note',
    title: 'Thoughts on focus',
    description: 'A reminder to leave a little room between tasks.',
    content: 'Collect moments of clarity.\n\nProtect the first hour for the work that matters most. Leave a little room between tasks so good ideas have somewhere to land.',
    url: '',
    tags: ['work', 'reflection'],
    collection: 'Work notes',
    favorite: true,
    createdAt: daysAgo(3),
    updatedAt: daysAgo(3),
    fileName: '',
    language: '',
  },
  {
    id: 'item-product-strategy',
    type: 'pdf',
    title: 'Product strategy, spring notes',
    description: 'A collected set of product principles and planning notes.',
    content: 'Clarity over complexity. Make the useful path the obvious path.',
    url: '',
    tags: ['product', 'planning'],
    collection: 'Work notes',
    favorite: false,
    createdAt: daysAgo(4),
    updatedAt: daysAgo(4),
    fileName: 'product-strategy-spring.pdf',
    language: '',
  },
  {
    id: 'item-color-study',
    type: 'image',
    title: 'Late afternoon color study',
    description: 'Muted sage and terracotta palette reference.',
    content: '',
    url: '',
    tags: ['color', 'inspiration'],
    collection: 'Design ideas',
    favorite: false,
    createdAt: daysAgo(6),
    updatedAt: daysAgo(6),
    fileName: 'color-study.png',
    language: '',
  },
  {
    id: 'item-array-helper',
    type: 'code',
    title: 'Group items by key',
    description: 'A small TypeScript helper for turning a list into grouped records.',
    content: 'const groupBy = <T, K extends string>(\n  items: T[],\n  getKey: (item: T) => K,\n) => items.reduce<Record<K, T[]>>((groups, item) => {\n  const key = getKey(item)\n  ;(groups[key] ??= []).push(item)\n  return groups\n}, {} as Record<K, T[]>)',
    url: '',
    tags: ['typescript', 'utility'],
    collection: 'Work notes',
    favorite: false,
    createdAt: daysAgo(8),
    updatedAt: daysAgo(8),
    fileName: '',
    language: 'TypeScript',
  },
  {
    id: 'item-small-gardens',
    type: 'website',
    title: 'A guide to small, generous gardens',
    description: 'Practical ideas for making a compact outdoor space feel abundant.',
    content: 'Layered planting, borrowed views, and small seating areas make a garden feel welcoming.',
    url: 'https://gardenjournal.example/compact-gardens',
    tags: ['gardens', 'ideas'],
    collection: 'Reading list',
    favorite: false,
    createdAt: daysAgo(11),
    updatedAt: daysAgo(11),
    fileName: '',
    language: '',
  },
]

const STORAGE_KEY = 'snapit-saved-items'

export function loadSavedItems(): SavedItem[] {
  const saved = window.localStorage.getItem(STORAGE_KEY)
  if (!saved) return starterItems

  try {
    const parsed: unknown = JSON.parse(saved)
    if (Array.isArray(parsed)) {
      const validatedItems = parsed.filter(isSavedItem)
      if (validatedItems.length === parsed.length) return validatedItems
    }
  } catch {
    return starterItems
  }

  return starterItems
}

export function persistSavedItems(items: SavedItem[]): void {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(items))
}
