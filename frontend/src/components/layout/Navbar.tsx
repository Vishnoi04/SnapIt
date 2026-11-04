import { useState } from 'react'

type NavbarProps = {
    theme: 'dark' | 'light'
    onToggleTheme: () => void
}

function Navbar({ theme, onToggleTheme }: NavbarProps) {
    const [menuOpen, setMenuOpen] = useState(false)

    return (
        <header className="navbar">
            <a href="/" className="navbar-brand">
                <span className="brand-icon">S</span>
                <span>snapit</span>
            </a>

            <button
                type="button"
                className="menu-toggle"
                aria-expanded={menuOpen}
                aria-controls="primary-navigation"
                aria-label={menuOpen ? 'Close navigation menu' : 'Open navigation menu'}
                onClick={() => setMenuOpen((isOpen) => !isOpen)}
            >
                <span />
                <span />
            </button>

            <nav id="primary-navigation" className={`navbar-links${menuOpen ? ' navbar-links-open' : ''}`}>
                <a href="#features" onClick={() => setMenuOpen(false)}>Features</a>
                <a href="#how-it-works" onClick={() => setMenuOpen(false)}>How it works</a>
                <a href="#pricing" onClick={() => setMenuOpen(false)}>Pricing</a>
            </nav>

            <div className="navbar-actions">
                <a href="/login" className="login-link">Log in</a>
                <a href="/signup" className="signup-button">Get Started</a>
                <button
                    className="theme-toggle"
                    type="button"
                    onClick={onToggleTheme}
                    aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
                    title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
                >
                    <span aria-hidden="true">{theme === 'dark' ? '☼' : '☾'}</span>
                </button>
            </div>
        </header>
    )
}
export default Navbar