import { useEffect, useState, type FormEvent } from 'react'
import { FileText, X } from 'lucide-react'
import type { SavedItem, SavedItemType } from '../../types/savedItem'
import { savedItemTypeLabels, savedItemTypes } from '../../types/savedItem'

type ItemEditorDialogProps = {
  item?: SavedItem
  collections: string[]
  onClose: () => void
  onSave: (item: SavedItem) => void
}

const languages = ['Java', 'JavaScript', 'TypeScript', 'Python', 'SQL', 'HTML', 'CSS', 'JSON']

function ItemEditorDialog({ item, collections, onClose, onSave }: ItemEditorDialogProps) {
  const [type, setType] = useState<SavedItemType>(item?.type ?? 'website')
  const [title, setTitle] = useState(item?.title ?? '')
  const [description, setDescription] = useState(item?.description ?? '')
  const [content, setContent] = useState(item?.content ?? '')
  const [url, setUrl] = useState(item?.url ?? '')
  const [tagsText, setTagsText] = useState(item?.tags.join(', ') ?? '')
  const [collection, setCollection] = useState(item?.collection ?? '')
  const [language, setLanguage] = useState(item?.language ?? 'TypeScript')
  const [fileName, setFileName] = useState(item?.fileName ?? '')
  const [previewUrl, setPreviewUrl] = useState('')
  const [favorite, setFavorite] = useState(item?.favorite ?? false)
  const [error, setError] = useState('')

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl)
    }
  }, [previewUrl])

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!title.trim()) {
      setError('Add a title for this item.')
      return
    }

    if (type === 'website') {
      try {
        const parsedUrl = new URL(url)
        if (parsedUrl.protocol !== 'https:' && parsedUrl.protocol !== 'http:') {
          setError('Use a web address that starts with http:// or https://.')
          return
        }
      } catch {
        setError('Enter a valid website address.')
        return
      }
    }

    if ((type === 'image' || type === 'pdf') && !fileName.trim()) {
      setError(`Choose a ${type === 'pdf' ? 'PDF' : 'image'} file to continue.`)
      return
    }

    if ((type === 'note' || type === 'code') && !content.trim()) {
      setError(type === 'note' ? 'Add some note text to continue.' : 'Add code to continue.')
      return
    }

    const now = new Date().toISOString()
    onSave({
      id: item?.id ?? `item-${crypto.randomUUID()}`,
      type,
      title: title.trim(),
      description: description.trim(),
      content: type === 'website' || type === 'note' || type === 'code' ? content : '',
      url: type === 'website' ? url.trim() : '',
      tags: tagsText.split(',').map((tag) => tag.trim()).filter(Boolean),
      collection,
      favorite,
      createdAt: item?.createdAt ?? now,
      updatedAt: now,
      fileName: type === 'image' || type === 'pdf' ? fileName : '',
      language: type === 'code' ? language : '',
    })
  }

  return (
    <div className="dialog-backdrop" onMouseDown={(event) => {
      if (event.target === event.currentTarget) onClose()
    }}>
      <section className="item-editor dialog-panel" role="dialog" aria-modal="true" aria-labelledby="item-editor-title">
        <header className="dialog-header">
          <div>
            <p className="dashboard-eyebrow">{item ? 'YOUR LIBRARY' : 'SAVE SOMETHING GOOD'}</p>
            <h2 id="item-editor-title">{item ? 'Edit saved item' : 'Add a saved item'}</h2>
          </div>
          <button type="button" className="icon-button" onClick={onClose} aria-label="Close dialog">
            <X size={18} />
          </button>
        </header>

        <div className="item-type-picker" aria-label="Content type">
          {savedItemTypes.map((itemType) => (
            <button
              type="button"
              key={itemType}
              className={type === itemType ? 'type-picker-active' : ''}
              onClick={() => {
                setType(itemType)
                setError('')
              }}
            >
              {savedItemTypeLabels[itemType]}
            </button>
          ))}
        </div>

        <form className="item-editor-form" onSubmit={handleSubmit}>
          <label className="dashboard-field">
            <span>Title <b aria-hidden="true">*</b></span>
            <input autoFocus value={title} onChange={(event) => setTitle(event.target.value)} maxLength={120} />
          </label>

          {type === 'website' && (
            <label className="dashboard-field">
              <span>Website URL <b aria-hidden="true">*</b></span>
              <input type="url" value={url} onChange={(event) => setUrl(event.target.value)} placeholder="https://example.com/article" />
            </label>
          )}

          {(type === 'image' || type === 'pdf') && (
            <label className="dashboard-field">
              <span>{type === 'pdf' ? 'PDF file' : 'Image file'} <b aria-hidden="true">*</b></span>
              <input
                type="file"
                accept={type === 'pdf' ? 'application/pdf,.pdf' : 'image/*'}
                onChange={(event) => {
                  const file = event.target.files?.[0]
                  setFileName(file?.name ?? '')
                  setPreviewUrl(file && type === 'image' ? URL.createObjectURL(file) : '')
                }}
              />
              {fileName && <small className="file-selection">{fileName} · saved as a local demo reference</small>}
              {type === 'image' && previewUrl && <img className="editor-image-preview" src={previewUrl} alt="Selected image preview" />}
              {type === 'pdf' && fileName && <div className="editor-pdf-preview"><FileText size={23} /><span><strong>{fileName}</strong><small>PDF preview placeholder</small></span></div>}
            </label>
          )}

          <label className="dashboard-field">
            <span>Description</span>
            <textarea value={description} onChange={(event) => setDescription(event.target.value)} rows={2} maxLength={300} />
          </label>

          {(type === 'note' || type === 'code') && (
            <label className="dashboard-field">
              <span>{type === 'note' ? 'Note text' : 'Code'} <b aria-hidden="true">*</b></span>
              {type === 'code' && (
                <select value={language} onChange={(event) => setLanguage(event.target.value)}>
                  {languages.map((option) => <option key={option}>{option}</option>)}
                </select>
              )}
              <textarea
                className={type === 'code' ? 'code-input' : ''}
                value={content}
                onChange={(event) => setContent(event.target.value)}
                rows={type === 'code' ? 7 : 5}
                maxLength={10000}
              />
            </label>
          )}

          <div className="dashboard-form-row">
            <label className="dashboard-field">
              <span>Tags <small>Separate with commas</small></span>
              <input value={tagsText} onChange={(event) => setTagsText(event.target.value)} placeholder="design, reading" />
            </label>
            <label className="dashboard-field">
              <span>Collection</span>
              <select value={collection} onChange={(event) => setCollection(event.target.value)}>
                <option value="">Unsorted</option>
                {collections.map((name) => <option key={name}>{name}</option>)}
              </select>
            </label>
          </div>

          <label className="checkbox-label dashboard-favorite-toggle">
            <input type="checkbox" checked={favorite} onChange={(event) => setFavorite(event.target.checked)} />
            <span>Add to favorites</span>
          </label>

          {error && <p className="dashboard-form-error" role="alert">{error}</p>}

          <footer className="dialog-actions">
            <button type="button" className="dashboard-button dashboard-button-quiet" onClick={onClose}>Cancel</button>
            <button type="submit" className="dashboard-button dashboard-button-primary">{item ? 'Save changes' : 'Save item'}</button>
          </footer>
        </form>
      </section>
    </div>
  )
}

export default ItemEditorDialog
