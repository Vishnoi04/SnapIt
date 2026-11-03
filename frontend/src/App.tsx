import './App.css'


function App() {
  return (
    <main
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '24px',
        textAlign: 'center',
        background: 'var(--bg-primary)',
        color: 'var(--text-primary)',
      }}
    >
      <p
        style={{
          color: 'var(--accent-secondary)',
          letterSpacing: '0.2em',
          textTransform: 'uppercase',
          fontSize: '12px',
        }}
      >
        Your personal knowledge space
      </p>

      <h1
        style={{
          fontSize: 'clamp(48px, 8vw, 88px)',
          letterSpacing: '-0.06em',
          margin: '12px 0',
        }}
      >
        Save anything.
        <br />
        Find it instantly.
      </h1>

      <p
        style={{
          maxWidth: '480px',
          color: 'var(--text-secondary)',
          lineHeight: 1.8,
        }}
      >
        Websites, images, PDFs, notes, and code — everything worth
        keeping, all in one beautiful space.
      </p>

      <button
        style={{
          marginTop: '24px',
          padding: '14px 24px',
          border: 'none',
          borderRadius: '8px',
          background: 'var(--accent-primary)',
          color: '#FFFFFF',
          fontWeight: 600,
        }}
      >
        Explore SnapIt
      </button>
    </main>
  )
}

export default App
