import { useEffect, useRef, useState } from 'react'
import {
  ArrowLeft,
  ArrowUpDown,
  Bookmark,
  BookOpen,
  Check,
  ChevronDown,
  Clock3,
  Code2,
  FileImage,
  FileText,
  Filter,
  Folder,
  Grid2X2,
  Heart,
  Image,
  LayoutDashboard,
  List,
  MoreHorizontal,
  Plus,
  Search,
  Settings,
  Sparkles,
  Sun,
  StickyNote,
  Trash2,
  Upload,
  X,
  Globe2,
  Moon,
} from 'lucide-react'
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import ItemEditorDialog from '../../components/dashboard/ItemEditorDialog'
import { loadSavedItems, persistSavedItems, starterCollections } from '../../services/mockItems'
import type { SavedItem, SavedItemType } from '../../types/savedItem'
import { isSavedItemType, savedItemTypeLabels, savedItemTypes } from '../../types/savedItem'

type SortOrder = 'recent' | 'oldest' | 'title-asc' | 'title-desc'
type ViewMode = 'grid' | 'list'
type DashboardPageProps = {
  theme: 'dark' | 'light'
  onToggleTheme: () => void
}
const RECENT_WINDOW_START = Date.now() - 7 * 24 * 60 * 60 * 1000

const typeIcons: Record<SavedItemType, typeof Globe2> = {
  website: Globe2,
  image: FileImage,
  pdf: FileText,
  note: StickyNote,
  code: Code2,
}

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric' }).format(new Date(value))
}

function getDomain(url: string): string {
  try {
    return new URL(url).hostname.replace(/^www\./, '')
  } catch {
    return ''
  }
}

function DashboardPage({ theme, onToggleTheme }: DashboardPageProps) {
  const location = useLocation()
  const navigate = useNavigate()
  const [items, setItems] = useState<SavedItem[]>(loadSavedItems)
  const [query, setQuery] = useState('')
  const [sortOrder, setSortOrder] = useState<SortOrder>('recent')
  const [viewMode, setViewMode] = useState<ViewMode>('grid')
  const [filterOpen, setFilterOpen] = useState(false)
  const [activeFilter, setActiveFilter] = useState<SavedItemType | 'all'>('all')
  const [editorItem, setEditorItem] = useState<SavedItem | null>(null)
  const [editorOpen, setEditorOpen] = useState(false)
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [toast, setToast] = useState('')
  const [storageError, setStorageError] = useState(false)

  const path = location.pathname.replace(/\/+$/, '')
  const detailMatch = path.match(/^\/dashboard\/items\/([^/]+)$/)
  const detailItem = detailMatch ? items.find((item) => item.id === decodeURIComponent(detailMatch[1])) : undefined
  const routeTypeValue = path.match(/^\/dashboard\/type\/([^/]+)$/)?.[1]
  const routeType = routeTypeValue && isSavedItemType(routeTypeValue) ? routeTypeValue : undefined
  const routeTag = path.match(/^\/dashboard\/tag\/([^/]+)$/)?.[1]
  const routeCollection = path.match(/^\/dashboard\/collection\/([^/]+)$/)?.[1]
  const isFavorites = path === '/dashboard/favorites'
  const isUnsorted = path === '/dashboard/unsorted'
  const isRecent = path === '/dashboard/recent'
  const filterTag = routeTag ? decodeURIComponent(routeTag) : ''
  const filterCollection = routeCollection ? decodeURIComponent(routeCollection) : ''
  const searchInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    function focusSearch(event: KeyboardEvent) {
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault()
        searchInputRef.current?.focus()
      }
    }

    window.addEventListener('keydown', focusSearch)
    return () => window.removeEventListener('keydown', focusSearch)
  }, [])

  useEffect(() => {
    if (!toast) return
    const timer = window.setTimeout(() => setToast(''), 3200)
    return () => window.clearTimeout(timer)
  }, [toast])

  const selectedItem = detailItem

  const normalizedQuery = query.trim().toLowerCase()
  const filteredItems = items.filter((item) => {
    if (isFavorites && !item.favorite) return false
    if (isUnsorted && item.collection) return false
    if (isRecent && new Date(item.createdAt).getTime() < RECENT_WINDOW_START) return false
    if (routeType && item.type !== routeType) return false
    if (activeFilter !== 'all' && item.type !== activeFilter) return false
    if (filterTag && !item.tags.includes(filterTag)) return false
    if (filterCollection && item.collection !== filterCollection) return false
    if (!normalizedQuery) return true

    const searchable = [
      item.title,
      item.description,
      item.content,
      item.url,
      item.fileName,
      item.language,
      item.collection,
      ...item.tags,
    ].join(' ').toLowerCase()
    return searchable.includes(normalizedQuery)
  }).sort((first, second) => {
    if (sortOrder === 'oldest') return first.createdAt.localeCompare(second.createdAt)
    if (sortOrder === 'title-asc') return first.title.localeCompare(second.title)
    if (sortOrder === 'title-desc') return second.title.localeCompare(first.title)
    return second.createdAt.localeCompare(first.createdAt)
  })

  const pageTitle = selectedItem
    ? 'Item details'
    : isFavorites
      ? 'Favorites'
      : isUnsorted
        ? 'Unsorted'
        : isRecent
          ? 'Recently added'
          : filterCollection
            ? filterCollection
            : filterTag
              ? `Tag: ${filterTag}`
              : routeType
                ? `${savedItemTypeLabels[routeType]}s`
                : 'All saved items'

  function saveItem(item: SavedItem) {
    const exists = items.some((existing) => existing.id === item.id)
    const nextItems = exists
      ? items.map((existing) => existing.id === item.id ? item : existing)
      : [item, ...items]
    commitItems(nextItems)
    setEditorOpen(false)
    setEditorItem(null)
    setToast(editorItem ? 'Your changes are saved.' : 'Saved to your SnapIt library.')
  }

  function toggleFavorite(itemId: string) {
    commitItems(items.map((item) =>
      item.id === itemId ? { ...item, favorite: !item.favorite, updatedAt: new Date().toISOString() } : item,
    ))
  }

  function deleteItem(item: SavedItem) {
    if (!window.confirm(`Delete “${item.title}” from your library? This cannot be undone.`)) return
    commitItems(items.filter((entry) => entry.id !== item.id))
    if (selectedItem?.id === item.id) navigate('/dashboard')
    setToast('Item deleted.')
  }

  async function copyCode(code: string) {
    try {
      await navigator.clipboard.writeText(code)
      setToast('Code copied to clipboard.')
    } catch {
      setToast('Clipboard access is unavailable in this browser.')
    }
  }

  function openEditor(item?: SavedItem) {
    setEditorItem(item ?? null)
    setEditorOpen(true)
  }

  function commitItems(nextItems: SavedItem[]) {
    setItems(nextItems)
    try {
      persistSavedItems(nextItems)
      setStorageError(false)
    } catch {
      setStorageError(true)
    }
  }

  function showNextTaskMessage() {
    setToast('Collection management is coming in the next workspace task.')
  }

  return (
    <div className="dashboard-app">
      {sidebarOpen && <button className="dashboard-scrim" onClick={() => setSidebarOpen(false)} aria-label="Close navigation" />}
      <aside className={`dashboard-sidebar${sidebarOpen ? ' dashboard-sidebar-open' : ''}`}>
        <Link to="/" className="dashboard-brand">
          <span className="brand-icon">S</span><span>snapit</span>
        </Link>

        <nav className="sidebar-nav" aria-label="Main navigation">
          <p className="sidebar-section-label">WORKSPACE</p>
          <SidebarLink to="/dashboard" label="Overview" icon={LayoutDashboard} onClick={() => setSidebarOpen(false)} end />
          <SidebarLink to="/dashboard/items" label="All saved items" icon={Bookmark} onClick={() => setSidebarOpen(false)} />
          <SidebarLink to="/dashboard/unsorted" label="Unsorted" icon={Upload} onClick={() => setSidebarOpen(false)} />
          <SidebarLink to="/dashboard/favorites" label="Favorites" icon={Heart} onClick={() => setSidebarOpen(false)} />
          <SidebarLink to="/dashboard/recent" label="Recent items" icon={Clock3} onClick={() => setSidebarOpen(false)} />

          <div className="sidebar-section-heading">
            <p className="sidebar-section-label">COLLECTIONS</p>
            <button type="button" className="sidebar-add" onClick={showNextTaskMessage} aria-label="Add collection"><Plus size={14} /></button>
          </div>
          {starterCollections.map((collection) => (
            <SidebarLink
              key={collection}
              to={`/dashboard/collection/${encodeURIComponent(collection)}`}
              label={collection}
              icon={Folder}
              onClick={() => setSidebarOpen(false)}
            />
          ))}
          <button type="button" className="sidebar-nav-item sidebar-plain-button" onClick={showNextTaskMessage}>
            <Folder size={16} /><span>Manage collections</span>
          </button>

          <p className="sidebar-section-label sidebar-spaced">CONTENT TYPE</p>
          {savedItemTypes.map((type) => {
            const Icon = typeIcons[type]
            return (
              <SidebarLink
                key={type}
                to={`/dashboard/type/${type}`}
                label={savedItemTypeLabels[type]}
                icon={Icon}
                onClick={() => setSidebarOpen(false)}
              />
            )
          })}

          <p className="sidebar-section-label sidebar-spaced">YOUR TAGS</p>
          <div className="sidebar-tags">
            {[...new Set(items.flatMap((item) => item.tags))].slice(0, 6).map((tag) => (
              <Link
                key={tag}
                to={`/dashboard/tag/${encodeURIComponent(tag)}`}
                className={`sidebar-tag${filterTag === tag ? ' sidebar-tag-active' : ''}`}
                onClick={() => setSidebarOpen(false)}
              >
                <span />{tag}
              </Link>
            ))}
          </div>
        </nav>

        <div className="sidebar-bottom">
          <button type="button" className="premium-prompt" onClick={() => setToast('SnapIt Premium details are coming soon.')}>
            <span className="premium-icon"><Sparkles size={16} /></span>
            <span><strong>More room to grow</strong><small>Explore Premium</small></span>
            <ChevronDown size={14} className="premium-chevron" />
          </button>
          <button type="button" className="sidebar-nav-item sidebar-plain-button" onClick={showNextTaskMessage}><Settings size={16} /><span>Settings</span></button>
          <div className="sidebar-profile">
            <span className="profile-avatar">V</span>
            <span className="profile-copy"><strong>Vicky</strong><small>Personal space</small></span>
            <button type="button" onClick={() => setToast('You are using a local demo workspace.')} aria-label="Workspace information"><MoreHorizontal size={17} /></button>
          </div>
        </div>
      </aside>

      <main className="dashboard-main">
        <header className="dashboard-topbar">
          <button type="button" className="mobile-sidebar-toggle" onClick={() => setSidebarOpen(true)} aria-label="Open navigation"><LayoutDashboard size={19} /></button>
          <div className="dashboard-breadcrumb"><span>Workspace</span><span aria-hidden="true">/</span><strong>{pageTitle}</strong></div>
          <label className="dashboard-search">
            <Search size={16} />
            <input ref={searchInputRef} value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search anything you saved..." aria-label="Search saved items" />
            <kbd>⌘ K</kbd>
          </label>
          <button
            type="button"
            className="dashboard-theme-toggle"
            onClick={onToggleTheme}
            aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
            title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
          >
            {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
          </button>
          <button type="button" className="topbar-avatar" aria-label="Vicky profile">V</button>
        </header>

        <div className="dashboard-content">
          <div className="dashboard-page-heading">
            <div>
              <p className="dashboard-eyebrow">{selectedItem ? 'YOUR LIBRARY' : 'A LITTLE MORE ORGANIZED'}</p>
              <h1>{selectedItem ? selectedItem.title : 'A good place to think.'}</h1>
              <p className="dashboard-subtitle">
                {selectedItem
                  ? 'Everything you saved, with the context you need.'
                  : 'All the useful things you want to find again, together.'}
              </p>
            </div>
            {!selectedItem && (
              <button type="button" className="dashboard-button dashboard-button-primary add-item-button" onClick={() => openEditor()}>
                <Plus size={17} /> Add new item
              </button>
            )}
          </div>

          {storageError && <p className="dashboard-storage-warning" role="status">Browser storage is full or unavailable. Your latest changes may not persist.</p>}

          {selectedItem ? (
            <ItemDetails
              item={selectedItem}
              onBack={() => navigate('/dashboard')}
              onEdit={() => openEditor(selectedItem)}
              onDelete={() => deleteItem(selectedItem)}
              onToggleFavorite={() => toggleFavorite(selectedItem.id)}
              onCopy={() => copyCode(selectedItem.content)}
              onShare={() => setToast('Sharing is planned for a future release.')}
              onTag={(tag) => navigate(`/dashboard/tag/${encodeURIComponent(tag)}`)}
            />
          ) : (
            <>
              <section className="dashboard-stats" aria-label="Library summary">
                <StatCard label="Saved items" value={items.length} icon={Bookmark} detail="Everything in one place" />
                <StatCard label="Websites" value={items.filter((item) => item.type === 'website').length} icon={Globe2} detail="Ideas from around the web" />
                <StatCard label="Documents" value={items.filter((item) => item.type === 'pdf').length} icon={FileText} detail="PDFs and references" />
                <StatCard label="Collections" value={starterCollections.length} icon={Folder} detail="Curated spaces" />
              </section>

              <div className="saved-items-toolbar">
                <div>
                  <h2>{pageTitle}</h2>
                  <span>{filteredItems.length} {filteredItems.length === 1 ? 'item' : 'items'}</span>
                </div>
                <div className="saved-toolbar-actions">
                  <div className="filter-control">
                    <button type="button" className={`dashboard-button dashboard-button-secondary${filterOpen ? ' control-active' : ''}`} onClick={() => setFilterOpen((open) => !open)} aria-expanded={filterOpen}>
                      <Filter size={15} /> Filter <ChevronDown size={13} />
                    </button>
                    {filterOpen && (
                      <div className="filter-popover">
                        <p>CONTENT TYPE</p>
                        <button type="button" onClick={() => { setActiveFilter('all'); setFilterOpen(false) }}>All content {activeFilter === 'all' && <Check size={14} />}</button>
                        {savedItemTypes.map((type) => (
                          <button key={type} type="button" onClick={() => { setActiveFilter(type); setFilterOpen(false) }}>
                            {savedItemTypeLabels[type]} {activeFilter === type && <Check size={14} />}
                          </button>
                        ))}
                        <div className="filter-popover-divider" />
                        <p>QUICK FILTERS</p>
                        <button type="button" onClick={() => { setActiveFilter('all'); navigate('/dashboard/favorites'); setFilterOpen(false) }}>Favorites</button>
                        <button type="button" onClick={() => { setActiveFilter('all'); navigate('/dashboard/unsorted'); setFilterOpen(false) }}>Unsorted</button>
                      </div>
                    )}
                  </div>
                  <label className="sort-control">
                    <ArrowUpDown size={14} />
                    <select value={sortOrder} onChange={(event) => setSortOrder(event.target.value as SortOrder)} aria-label="Sort saved items">
                      <option value="recent">Recently added</option>
                      <option value="oldest">Oldest first</option>
                      <option value="title-asc">Title A–Z</option>
                      <option value="title-desc">Title Z–A</option>
                    </select>
                  </label>
                  <div className="view-toggle" aria-label="Saved item layout">
                    <button type="button" className={viewMode === 'grid' ? 'view-active' : ''} onClick={() => setViewMode('grid')} aria-label="Grid view" aria-pressed={viewMode === 'grid'}><Grid2X2 size={16} /></button>
                    <button type="button" className={viewMode === 'list' ? 'view-active' : ''} onClick={() => setViewMode('list')} aria-label="List view" aria-pressed={viewMode === 'list'}><List size={17} /></button>
                  </div>
                </div>
              </div>

              {filteredItems.length ? (
                <section className={`saved-items-grid${viewMode === 'list' ? ' saved-items-list' : ''}`} aria-label="Saved items">
                  {filteredItems.map((item) => (
                    <SavedItemCard
                      key={item.id}
                      item={item}
                      onOpen={() => navigate(`/dashboard/items/${encodeURIComponent(item.id)}`)}
                      onToggleFavorite={() => toggleFavorite(item.id)}
                      onEdit={() => openEditor(item)}
                      onDelete={() => deleteItem(item)}
                      onTag={(tag) => navigate(`/dashboard/tag/${encodeURIComponent(tag)}`)}
                    />
                  ))}
                </section>
              ) : (
                <section className="dashboard-empty-state">
                  <span><Search size={22} /></span>
                  <h3>{query ? 'Nothing found this time.' : 'A little room for something new.'}</h3>
                  <p>{query ? 'Try another search or clear the filters.' : 'Save something useful and it will be waiting here when you need it.'}</p>
                  {query
                    ? <button className="dashboard-button dashboard-button-secondary" onClick={() => { setQuery(''); setActiveFilter('all'); navigate('/dashboard') }} type="button">Clear search</button>
                    : <button className="dashboard-button dashboard-button-primary" onClick={() => openEditor()} type="button"><Plus size={16} /> Add your first item</button>}
                </section>
              )}
            </>
          )}
        </div>
      </main>

      {editorOpen && (
        <ItemEditorDialog
          item={editorItem ?? undefined}
          collections={starterCollections}
          onClose={() => { setEditorOpen(false); setEditorItem(null) }}
          onSave={saveItem}
        />
      )}
      {toast && <div className="dashboard-toast" role="status"><Check size={15} />{toast}<button type="button" onClick={() => setToast('')} aria-label="Dismiss message"><X size={14} /></button></div>}
    </div>
  )
}

function SidebarLink({
  to,
  label,
  icon: Icon,
  end = false,
  onClick,
}: {
  to: string
  label: string
  icon: typeof LayoutDashboard
  end?: boolean
  onClick: () => void
}) {
  return (
    <NavLink to={to} end={end} className={({ isActive }) => `sidebar-nav-item${isActive ? ' sidebar-nav-active' : ''}`} onClick={onClick}>
      <Icon size={16} strokeWidth={1.8} /><span>{label}</span>
    </NavLink>
  )
}

function StatCard({
  label,
  value,
  icon: Icon,
  detail,
}: {
  label: string
  value: number
  icon: typeof Bookmark
  detail: string
}) {
  return (
    <article className="dashboard-stat-card">
      <span className="stat-icon"><Icon size={16} /></span>
      <span className="stat-label">{label}</span>
      <strong>{value.toString().padStart(2, '0')}</strong>
      <span className="stat-detail">{detail}</span>
    </article>
  )
}

function SavedItemCard({
  item,
  onOpen,
  onToggleFavorite,
  onEdit,
  onDelete,
  onTag,
}: {
  item: SavedItem
  onOpen: () => void
  onToggleFavorite: () => void
  onEdit: () => void
  onDelete: () => void
  onTag: (tag: string) => void
}) {
  const [menuOpen, setMenuOpen] = useState(false)
  const TypeIcon = typeIcons[item.type]

  return (
    <article className={`saved-item-card saved-card-${item.type}`}>
      <button type="button" className="saved-card-open" onClick={onOpen} aria-label={`Open ${item.title}`}>
        {item.type === 'image' ? (
          <div className={`saved-card-art ${item.id.includes('color') ? 'saved-card-art-warm' : ''}`}>
            <span>{item.id.includes('color') ? 'COLOR / STUDY' : 'FORM & FUNCTION'}</span>
            <i />
          </div>
        ) : item.type === 'website' ? (
          <div className="saved-card-website-preview">
            <span><Globe2 size={13} /> {getDomain(item.url)}</span>
            <strong>{item.title}</strong>
            <small>{item.description}</small>
          </div>
        ) : item.type === 'pdf' ? (
          <div className="saved-card-pdf-preview">
            <span className="pdf-preview-icon"><FileText size={22} /></span>
            <span className="pdf-file-label">PDF DOCUMENT</span>
            <strong>{item.fileName || item.title}</strong>
            <small>Saved reference</small>
          </div>
        ) : item.type === 'code' ? (
          <div className="saved-card-code-preview">
            <span><Code2 size={13} /> {item.language}</span>
            <code>{item.content.split('\n').slice(0, 4).join('\n')}</code>
          </div>
        ) : (
          <div className="saved-card-note-preview">
            <span>“</span>
            <p>{item.content || item.description}</p>
          </div>
        )}
      </button>

      <div className="saved-card-body">
        <div className="saved-card-title-row">
          <div className="saved-card-title-copy">
            <span className="saved-item-type"><TypeIcon size={12} />{savedItemTypeLabels[item.type]}</span>
            <button type="button" className="saved-card-title" onClick={onOpen}>{item.title}</button>
          </div>
          <div className="saved-card-actions">
            <button type="button" className={`card-icon-button${item.favorite ? ' card-favorited' : ''}`} onClick={onToggleFavorite} aria-label={item.favorite ? 'Remove from favorites' : 'Add to favorites'} aria-pressed={item.favorite}><Heart size={16} fill={item.favorite ? 'currentColor' : 'none'} /></button>
            <div className="card-menu-wrap">
              <button type="button" className="card-icon-button" onClick={() => setMenuOpen((open) => !open)} aria-expanded={menuOpen} aria-label="More item actions"><MoreHorizontal size={17} /></button>
              {menuOpen && (
                <div className="card-action-menu">
                  <button type="button" onClick={() => { setMenuOpen(false); onOpen() }}>Open details</button>
                  <button type="button" onClick={() => { setMenuOpen(false); onEdit() }}>Edit item</button>
                  <button type="button" className="delete-action" onClick={() => { setMenuOpen(false); onDelete() }}><Trash2 size={13} />Delete</button>
                </div>
              )}
            </div>
          </div>
        </div>
        <p className="saved-card-description">{item.description || item.content}</p>
        {item.type === 'website' && item.url && <a className="saved-card-domain" href={item.url} target="_blank" rel="noreferrer" onClick={(event) => event.stopPropagation()}>{getDomain(item.url)} <span aria-hidden="true">↗</span></a>}
        <div className="saved-card-tags">
          {item.tags.slice(0, 3).map((tag) => <button key={tag} type="button" onClick={() => onTag(tag)}>#{tag}</button>)}
          {item.collection && <span className="saved-card-collection"><Folder size={11} />{item.collection}</span>}
        </div>
        <div className="saved-card-meta"><span>Saved {formatDate(item.createdAt)}</span>{item.type === 'pdf' && <span>PDF</span>}</div>
      </div>
    </article>
  )
}

function ItemDetails({
  item,
  onBack,
  onEdit,
  onDelete,
  onToggleFavorite,
  onCopy,
  onShare,
  onTag,
}: {
  item: SavedItem
  onBack: () => void
  onEdit: () => void
  onDelete: () => void
  onToggleFavorite: () => void
  onCopy: () => void
  onShare: () => void
  onTag: (tag: string) => void
}) {
  const TypeIcon = typeIcons[item.type]
  return (
    <section className="item-details">
      <button type="button" className="details-back" onClick={onBack}><ArrowLeft size={15} /> Back to your library</button>
      <div className="details-card">
        <div className="details-topline"><span className="saved-item-type"><TypeIcon size={13} />{savedItemTypeLabels[item.type]}</span><span>Saved {formatDate(item.createdAt)}</span></div>
        {item.type === 'image' && <div className="details-image-preview"><Image size={34} /><span>{item.fileName || 'Image reference saved locally'}</span></div>}
        {item.type === 'pdf' && <div className="details-pdf-preview"><FileText size={29} /><span><strong>{item.fileName || item.title}</strong><small>PDF preview · file reference only</small></span></div>}
        {item.type === 'website' && <div className="details-website-preview"><BookOpen size={20} /><span>{getDomain(item.url)}</span></div>}
        <div className="details-title-row">
          <h2>{item.title}</h2>
          <div>
            <button type="button" className={`dashboard-button dashboard-button-secondary${item.favorite ? ' detail-favorited' : ''}`} onClick={onToggleFavorite}><Heart size={15} fill={item.favorite ? 'currentColor' : 'none'} />{item.favorite ? 'Favorited' : 'Favorite'}</button>
            <button type="button" className="dashboard-button dashboard-button-secondary" onClick={onEdit}>Edit</button>
            <button type="button" className="dashboard-button dashboard-button-secondary" onClick={onShare}>Share</button>
            <button type="button" className="dashboard-button dashboard-button-danger" onClick={onDelete}>Delete</button>
          </div>
        </div>
        {item.url && <a className="details-source-link" href={item.url} target="_blank" rel="noreferrer">{item.url} <span aria-hidden="true">↗</span></a>}
        {item.description && <p className="details-description">{item.description}</p>}
        {item.type === 'note' && <div className="details-content-text">{item.content}</div>}
        {item.type === 'website' && item.content && <div className="details-content-text"><span className="details-section-label">SAVED EXCERPT</span>{item.content}</div>}
        {item.type === 'code' && (
          <div className="details-code">
            <div><span><Code2 size={14} />{item.language}</span><button type="button" onClick={onCopy}>Copy code</button></div>
            <pre><code>{item.content}</code></pre>
          </div>
        )}
        {item.tags.length > 0 && <div className="details-tags"><span className="details-section-label">TAGS</span>{item.tags.map((tag) => <button type="button" key={tag} onClick={() => onTag(tag)}>#{tag}</button>)}</div>}
        <div className="details-metadata"><span><Folder size={14} />{item.collection || 'Unsorted'}</span><span>Added {formatDate(item.createdAt)}</span><span>Updated {formatDate(item.updatedAt)}</span></div>
        {item.type === 'website' && item.url && <a className="dashboard-button dashboard-button-primary details-open-source" href={item.url} target="_blank" rel="noreferrer">Open original website <span aria-hidden="true">↗</span></a>}
      </div>
    </section>
  )
}

export default DashboardPage
