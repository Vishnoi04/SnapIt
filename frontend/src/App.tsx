import { useEffect, useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AuthPages from './pages/auth/AuthPages'
import LandingPage from './pages/LandingPage'

type Theme = 'dark' | 'light'

function readSavedTheme(): Theme {
  return window.localStorage.getItem('snapit-theme') === 'light' ? 'light' : 'dark'
}

function App() {
  const [theme, setTheme] = useState<Theme>(readSavedTheme)

  useEffect(() => {
    window.localStorage.setItem('snapit-theme', theme)
  }, [theme])

  const toggleTheme = () => {
    setTheme((currentTheme) => (currentTheme === 'dark' ? 'light' : 'dark'))
  }

  return (
    <div className="app-shell" data-theme={theme}>
      <BrowserRouter>
        <Routes>
          <Route
            path="/"
            element={<LandingPage theme={theme} onToggleTheme={toggleTheme} />}
          />
          <Route
            path="/login"
            element={<AuthPages page="login" theme={theme} onToggleTheme={toggleTheme} />}
          />
          <Route
            path="/signup"
            element={<AuthPages page="signup" theme={theme} onToggleTheme={toggleTheme} />}
          />
          <Route
            path="/forgot-password"
            element={<AuthPages page="forgot-password" theme={theme} onToggleTheme={toggleTheme} />}
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </div>
  )
}

export default App