import { useEffect, useState, type ReactNode } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import AuthPages from './pages/auth/AuthPages'
import LandingPage from './pages/LandingPage'
import DashboardPage from './pages/dashboard/DashboardPage'
import { PricingPage } from './pages/dashboard/WorkspacePages'

type Theme = 'dark' | 'light'

function readSavedTheme(): Theme {
  return window.localStorage.getItem('snapit-theme') === 'light' ? 'light' : 'dark'
}

function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <div className="app-shell" data-theme={readSavedTheme()}><div className="auth-page"><div className="auth-card" style={{ display: 'grid', placeItems: 'center' }}>Loading your SnapIt library…</div></div></div>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}

function GuestRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <div className="app-shell" data-theme={readSavedTheme()}><div className="auth-page"><div className="auth-card" style={{ display: 'grid', placeItems: 'center' }}>Loading…</div></div></div>
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  return children
}

function AppRoutes({ theme, onToggleTheme }: { theme: Theme; onToggleTheme: () => void }) {
  return (
    <Routes>
      <Route
        path="/"
        element={<LandingPage theme={theme} onToggleTheme={onToggleTheme} />}
      />
      <Route
        path="/login"
        element={<GuestRoute><AuthPages page="login" theme={theme} onToggleTheme={onToggleTheme} /></GuestRoute>}
      />
      <Route
        path="/signup"
        element={<GuestRoute><AuthPages page="signup" theme={theme} onToggleTheme={onToggleTheme} /></GuestRoute>}
      />
      <Route
        path="/forgot-password"
        element={<GuestRoute><AuthPages page="forgot-password" theme={theme} onToggleTheme={onToggleTheme} /></GuestRoute>}
      />
      <Route
        path="/pricing"
        element={<PricingPage theme={theme} onToggleTheme={onToggleTheme} />}
      />
      <Route
        path="/dashboard/*"
        element={<ProtectedRoute><DashboardPage theme={theme} onToggleTheme={onToggleTheme} /></ProtectedRoute>}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
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
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes theme={theme} onToggleTheme={toggleTheme} />
        </BrowserRouter>
      </AuthProvider>
    </div>
  )
}

export default App