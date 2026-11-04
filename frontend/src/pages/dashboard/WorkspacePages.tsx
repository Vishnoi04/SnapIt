import { useState, type FormEvent } from 'react'
import {
  ArrowUpRight,
  Check,
  Folder,
  Heart,
  Image,
  LockKeyhole,
  Plus,
  Settings,
  Sparkles,
  Tag,
  Trash2,
  UserRound,
  X,
} from 'lucide-react'
import { Link } from 'react-router-dom'
import type { Collection } from '../../types/collection'
import type { SavedItem } from '../../types/savedItem'
import type { UserProfile } from '../../types/user'
import type { UserPreferences } from '../../services/workspaceStorage'

type CollectionPageProps = {
  collections: Collection[]
  items: SavedItem[]
  onSave: (collection: Collection) => void
  onDelete: (collection: Collection) => void
  onOpen: (collection: Collection) => void
}

export function CollectionsPage({ collections, items, onSave, onDelete, onOpen }: CollectionPageProps) {
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Collection | undefined>()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState('')

  function openEditor(collection?: Collection) {
    setEditing(collection)
    setName(collection?.name ?? '')
    setDescription(collection?.description ?? '')
    setError('')
    setDialogOpen(true)
  }

  function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const normalizedName = name.trim()
    if (!normalizedName) {
      setError('Give this collection a name.')
      return
    }
    if (collections.some((collection) => collection.id !== editing?.id && collection.name.toLowerCase() === normalizedName.toLowerCase())) {
      setError('A collection with this name already exists.')
      return
    }
    onSave({
      id: editing?.id ?? `collection-${crypto.randomUUID()}`,
      name: normalizedName,
      description: description.trim(),
      createdAt: editing?.createdAt ?? new Date().toISOString(),
    })
    setDialogOpen(false)
  }

  return (
    <>
      <div className="workspace-section-heading">
        <div><p className="dashboard-eyebrow">YOUR SPACES</p><h2>Collections</h2><p>Give related ideas a place of their own.</p></div>
        <button type="button" className="dashboard-button dashboard-button-primary" onClick={() => openEditor()}><Plus size={15} /> New collection</button>
      </div>
      {collections.length ? (
        <div className="collection-grid">
          {collections.map((collection, index) => {
            const count = items.filter((item) => item.collection === collection.name).length
            return (
              <article className={`collection-card collection-card-${index % 3}`} key={collection.id}>
                <div className="collection-card-art"><Folder size={21} /><span>{String(count).padStart(2, '0')} ITEMS</span><i /></div>
                <div className="collection-card-copy">
                  <div><h3>{collection.name}</h3><span>{count} {count === 1 ? 'item' : 'items'}</span></div>
                  <p>{collection.description || 'A space for references you want to keep close.'}</p>
                  <div className="collection-card-actions">
                    <button type="button" className="dashboard-button dashboard-button-secondary" onClick={() => onOpen(collection)}>Open collection</button>
                    <button type="button" className="workspace-icon-button" onClick={() => openEditor(collection)} aria-label={`Edit ${collection.name}`}><Settings size={15} /></button>
                    <button type="button" className="workspace-icon-button workspace-delete-button" onClick={() => onDelete(collection)} aria-label={`Delete ${collection.name}`}><Trash2 size={15} /></button>
                  </div>
                </div>
              </article>
            )
          })}
        </div>
      ) : (
        <WorkspaceEmpty icon={Folder} title="Start a collection" description="Create a space to group the things you want to find together." action="Create a collection" onAction={() => openEditor()} />
      )}
      {dialogOpen && (
        <div className="dialog-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setDialogOpen(false) }}>
          <section className="workspace-dialog dialog-panel" role="dialog" aria-modal="true" aria-labelledby="collection-dialog-title">
            <header className="dialog-header">
              <div><p className="dashboard-eyebrow">YOUR SPACES</p><h2 id="collection-dialog-title">{editing ? 'Edit collection' : 'Create a collection'}</h2></div>
              <button type="button" className="icon-button" onClick={() => setDialogOpen(false)} aria-label="Close dialog"><X size={17} /></button>
            </header>
            <form className="item-editor-form" onSubmit={save}>
              <label className="dashboard-field"><span>Name <b aria-hidden="true">*</b></span><input autoFocus value={name} onChange={(event) => setName(event.target.value)} maxLength={60} /></label>
              <label className="dashboard-field"><span>Description</span><textarea rows={3} value={description} onChange={(event) => setDescription(event.target.value)} maxLength={220} placeholder="What belongs in this space?" /></label>
              {error && <p className="dashboard-form-error" role="alert">{error}</p>}
              <footer className="dialog-actions"><button type="button" className="dashboard-button dashboard-button-quiet" onClick={() => setDialogOpen(false)}>Cancel</button><button type="submit" className="dashboard-button dashboard-button-primary">{editing ? 'Save changes' : 'Create collection'}</button></footer>
            </form>
          </section>
        </div>
      )}
    </>
  )
}

export function TagsPage({
  items,
  onRename,
  onDelete,
}: {
  items: SavedItem[]
  onRename: (oldTag: string, newTag: string) => void
  onDelete: (tag: string) => void
}) {
  const [editingTag, setEditingTag] = useState('')
  const [tagName, setTagName] = useState('')
  const [error, setError] = useState('')
  const counts = [...new Set(items.flatMap((item) => item.tags))]
    .map((tag) => ({ tag, count: items.filter((item) => item.tags.includes(tag)).length }))
    .sort((first, second) => first.tag.localeCompare(second.tag))

  function saveTag(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const nextTag = tagName.trim().replace(/^#/, '')
    if (!nextTag) {
      setError('Tag name cannot be empty.')
      return
    }
    if (counts.some(({ tag }) => tag.toLowerCase() === nextTag.toLowerCase() && tag !== editingTag)) {
      setError('That tag already exists.')
      return
    }
    onRename(editingTag, nextTag)
    setEditingTag('')
    setTagName('')
    setError('')
  }

  return (
    <>
      <div className="workspace-section-heading">
        <div><p className="dashboard-eyebrow">MAKE CONNECTIONS</p><h2>Your tags</h2><p>Use tags to find ideas across different collections.</p></div>
        <span className="workspace-count">{counts.length} {counts.length === 1 ? 'tag' : 'tags'}</span>
      </div>
      {counts.length ? (
        <div className="tag-management-list">
          {counts.map(({ tag, count }) => (
            <article className="tag-management-row" key={tag}>
              <span className="tag-management-icon"><Tag size={15} /></span>
              <div className="tag-management-name"><Link to={`/dashboard/tag/${encodeURIComponent(tag)}`}>#{tag}</Link><span>{count} {count === 1 ? 'saved item' : 'saved items'}</span></div>
              <button type="button" className="dashboard-button dashboard-button-secondary" onClick={() => { setEditingTag(tag); setTagName(tag); setError('') }}>Rename</button>
              <button type="button" className="workspace-icon-button workspace-delete-button" onClick={() => onDelete(tag)} aria-label={`Delete tag ${tag}`}><Trash2 size={15} /></button>
            </article>
          ))}
        </div>
      ) : <WorkspaceEmpty icon={Tag} title="Tags make ideas easier to find" description="Add tags to saved items and manage them here." />}

      {editingTag && (
        <div className="dialog-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setEditingTag('') }}>
          <section className="workspace-dialog dialog-panel" role="dialog" aria-modal="true" aria-labelledby="tag-dialog-title">
            <header className="dialog-header"><div><p className="dashboard-eyebrow">MAKE CONNECTIONS</p><h2 id="tag-dialog-title">Rename tag</h2></div><button type="button" className="icon-button" onClick={() => setEditingTag('')} aria-label="Close dialog"><X size={17} /></button></header>
            <form className="item-editor-form" onSubmit={saveTag}>
              <label className="dashboard-field"><span>Tag name</span><input autoFocus value={tagName} onChange={(event) => setTagName(event.target.value)} /></label>
              {error && <p className="dashboard-form-error" role="alert">{error}</p>}
              <footer className="dialog-actions"><button type="button" className="dashboard-button dashboard-button-quiet" onClick={() => setEditingTag('')}>Cancel</button><button type="submit" className="dashboard-button dashboard-button-primary">Save tag</button></footer>
            </form>
          </section>
        </div>
      )}
    </>
  )
}

export function ProfilePage({
  profile,
  onSave,
}: {
  profile: UserProfile
  onSave: (profile: UserProfile) => boolean
}) {
  const [draft, setDraft] = useState(profile)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!draft.name.trim()) {
      setError('Your name cannot be empty.')
      setNotice('')
      return
    }
    if (!/^[a-zA-Z0-9_]{3,20}$/.test(draft.username)) {
      setError('Username must be 3–20 letters, numbers, or underscores.')
      setNotice('')
      return
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(draft.email)) {
      setError('Enter a valid email address.')
      setNotice('')
      return
    }
    const saved = onSave({ ...draft, name: draft.name.trim(), username: draft.username.trim(), email: draft.email.trim(), bio: draft.bio.trim() })
    if (!saved) {
      setError('Could not save your profile in browser storage. Your changes are available for this session only.')
      setNotice('')
      return
    }
    setError('')
    setNotice('Profile saved on this device.')
  }

  return (
    <>
      <div className="workspace-section-heading"><div><p className="dashboard-eyebrow">YOUR IDENTITY</p><h2>Profile</h2><p>Keep your personal details up to date.</p></div><span className="workspace-private-label"><LockKeyhole size={13} /> Personal profile</span></div>
      <div className="profile-layout">
        <section className="profile-editor-card">
          <div className="profile-avatar-large">{draft.name.trim().charAt(0).toUpperCase() || 'S'}</div>
          <p className="profile-local-note"><Image size={13} /> Profile photo upload will be available later.</p>
          <form className="profile-form" onSubmit={save}>
            <label className="dashboard-field"><span>Full name</span><input value={draft.name} onChange={(event) => setDraft({ ...draft, name: event.target.value })} maxLength={100} /></label>
            <label className="dashboard-field"><span>Username</span><div className="profile-input-prefix"><span>@</span><input value={draft.username} onChange={(event) => setDraft({ ...draft, username: event.target.value })} maxLength={20} /></div></label>
            <label className="dashboard-field"><span>Email</span><input type="email" value={draft.email} onChange={(event) => setDraft({ ...draft, email: event.target.value })} autoComplete="email" /></label>
            <label className="dashboard-field"><span>Bio <small>{draft.bio.length}/180</small></span><textarea rows={3} value={draft.bio} onChange={(event) => setDraft({ ...draft, bio: event.target.value })} maxLength={180} /></label>
            {error && <p className="dashboard-form-error" role="alert">{error}</p>}
            {notice && <p className="workspace-success" role="status"><Check size={14} />{notice}</p>}
            <button type="submit" className="dashboard-button dashboard-button-primary">Save profile</button>
          </form>
        </section>
        <aside className="profile-preview-card">
          <p className="dashboard-eyebrow">PROFILE PREVIEW</p>
          <div className="profile-preview-avatar">{draft.name.trim().charAt(0).toUpperCase() || 'S'}</div>
          <h3>{draft.name || 'Your name'}</h3><span>@{draft.username || 'username'}</span>
          <p>{draft.bio || 'A few words about what you like to collect.'}</p>
          <div className="profile-preview-private"><LockKeyhole size={13} /> Public profiles are not available yet.</div>
        </aside>
      </div>
    </>
  )
}

export function SettingsPage({
  theme,
  onToggleTheme,
  preferences,
  onPreferencesChange,
  onLogout,
}: {
  theme: 'dark' | 'light'
  onToggleTheme: () => void
  preferences: UserPreferences
  onPreferencesChange: (preferences: UserPreferences) => void
  onLogout: () => void
}) {
  return (
    <>
      <div className="workspace-section-heading"><div><p className="dashboard-eyebrow">MAKE YOUR SPACE YOURS</p><h2>Settings</h2><p>Manage your preferences for this local demo.</p></div><Settings size={20} className="settings-heading-icon" /></div>
      <div className="settings-sections">
        <section className="settings-group">
          <div className="settings-group-heading"><span><Sparkles size={16} /></span><div><h3>Appearance</h3><p>Choose how SnapIt looks on this device.</p></div></div>
          <div className="settings-row"><div><strong>Color theme</strong><small>Current theme: {theme === 'dark' ? 'Dark' : 'Light'}</small></div><button type="button" className="dashboard-button dashboard-button-secondary" onClick={onToggleTheme}>Switch to {theme === 'dark' ? 'light' : 'dark'}</button></div>
        </section>
        <section className="settings-group">
          <div className="settings-group-heading"><span><Heart size={16} /></span><div><h3>Notifications</h3><p>These preferences are saved in this browser only.</p></div></div>
          <PreferenceRow title="Weekly digest" description="A gentle summary of what you have saved." checked={preferences.weeklyDigest} onChange={(checked) => onPreferencesChange({ ...preferences, weeklyDigest: checked })} />
          <PreferenceRow title="Product updates" description="Hear about new features and improvements." checked={preferences.productUpdates} onChange={(checked) => onPreferencesChange({ ...preferences, productUpdates: checked })} />
        </section>
        <section className="settings-group">
          <div className="settings-group-heading"><span><LockKeyhole size={16} /></span><div><h3>Privacy</h3><p>Sharing and account controls are not connected yet.</p></div></div>
          <PreferenceRow title="Keep saved items private" description="New items stay in your personal library by default." checked={preferences.privateByDefault} onChange={(checked) => onPreferencesChange({ ...preferences, privateByDefault: checked })} />
          <div className="settings-row"><div><strong>Data and storage</strong><small>Your demo items are stored in this browser’s local storage.</small></div><span className="workspace-private-label"><LockKeyhole size={13} /> On this device</span></div>
        </section>
        <section className="settings-group settings-danger-zone">
          <div className="settings-group-heading"><span><UserRound size={16} /></span><div><h3>Account</h3><p>There is no active server session to end.</p></div></div>
          <div className="settings-row"><div><strong>Log out</strong><small>Authentication is a frontend-only placeholder.</small></div><button type="button" className="dashboard-button dashboard-button-danger" onClick={onLogout}>Log out</button></div>
        </section>
      </div>
    </>
  )
}

function PreferenceRow({ title, description, checked, onChange }: { title: string; description: string; checked: boolean; onChange: (checked: boolean) => void }) {
  return (
    <div className="settings-row"><div><strong>{title}</strong><small>{description}</small></div><button type="button" className={`switch-control${checked ? ' switch-on' : ''}`} role="switch" aria-checked={checked} aria-label={title} onClick={() => onChange(!checked)}><span /></button></div>
  )
}

export function PricingPage({
  theme,
  onToggleTheme,
}: {
  theme: 'dark' | 'light'
  onToggleTheme: () => void
}) {
  const [notice, setNotice] = useState(false)
  return (
    <div className="pricing-page">
      <header className="pricing-page-header">
        <Link to="/" className="dashboard-brand"><span className="brand-icon">S</span><span>snapit</span></Link>
        <nav><Link to="/#features">Features</Link><Link to="/#faq">FAQ</Link><Link to="/dashboard">Open workspace <ArrowUpRight size={14} /></Link><button className="theme-toggle" type="button" onClick={onToggleTheme} aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}>{theme === 'dark' ? '☼' : '☾'}</button></nav>
      </header>
      <main>
        <section className="pricing-page-hero"><p className="eyebrow">A PLAN FOR YOUR IDEAS</p><h1>Keep what matters.<br /><span>Choose your pace.</span></h1><p>Start with the essentials. Make more room as your personal library grows.</p></section>
        <section className="pricing-grid pricing-page-grid">
          <article className="plan-card"><p className="plan-label">FREE</p><h3>A thoughtful place to start.</h3><p className="plan-price">Free <span>— planned</span></p><p className="pricing-plan-intro">The everyday essentials for a personal knowledge space.</p><ul><li>Save webpages, images, and PDFs</li><li>Keep notes and code snippets</li><li>Basic collections and tags</li><li>Personal dashboard and basic search</li><li>Local-first demo experience</li></ul><Link className="button button-secondary" to="/signup">Get started</Link></article>
          <article className="plan-card plan-card-premium"><span className="premium-badge">A LITTLE MORE ROOM</span><p className="plan-label">PREMIUM</p><h3>For a library that keeps growing.</h3><p className="plan-price">Coming soon</p><p className="pricing-plan-intro">More capacity and advanced capabilities as they become available.</p><ul><li>Everything in Free</li><li>Higher storage limits</li><li>Advanced organization and search</li><li>Semantic search when available</li><li>Advanced OCR when available</li></ul><button className="button button-premium" type="button" onClick={() => setNotice(true)}>Premium details soon <ArrowUpRight size={14} /></button></article>
        </section>
        <section className="pricing-page-note"><h2>Thoughtful by design.</h2><p>Final prices, storage allowances, and premium feature availability have not been decided. Nothing here starts a subscription or processes a payment.</p>{notice && <p className="workspace-success" role="status"><Check size={14} /> We’ll share plan details when they’re ready.</p>}</section>
        <section className="pricing-faq"><p className="eyebrow">A FEW MORE DETAILS</p><details><summary>Will I need a credit card to try SnapIt?<span>+</span></summary><p>No payment flow is available yet. Final signup and plan terms will be shared when the service is ready.</p></details><details><summary>Are semantic search and OCR included today?<span>+</span></summary><p>No. They are planned capabilities and will be clearly marked when they become available.</p></details><details><summary>Can I change plans later?<span>+</span></summary><p>Plan management has not been implemented. Pricing and account controls are placeholders for now.</p></details></section>
      </main>
      <footer className="pricing-page-footer"><span>© {new Date().getFullYear()} SnapIt</span><Link to="/">Back to SnapIt</Link></footer>
    </div>
  )
}

function WorkspaceEmpty({ icon: Icon, title, description, action, onAction }: { icon: typeof Folder; title: string; description: string; action?: string; onAction?: () => void }) {
  return <section className="dashboard-empty-state"><span><Icon size={21} /></span><h3>{title}</h3><p>{description}</p>{action && onAction && <button className="dashboard-button dashboard-button-primary" onClick={onAction} type="button"><Plus size={15} />{action}</button>}</section>
}
