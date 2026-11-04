import { useState, type FormEvent, type ReactNode } from 'react'
import { Link } from 'react-router-dom'

type Theme = 'dark' | 'light'
type AuthPageName = 'login' | 'signup' | 'forgot-password'
type AuthPagesProps = {
  page: AuthPageName
  theme: Theme
  onToggleTheme: () => void
}
type FormErrors = Record<string, string>

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function AuthLayout({
  eyebrow,
  title,
  description,
  footer,
  theme,
  onToggleTheme,
  children,
}: {
  eyebrow: string
  title: string
  description: string
  footer: ReactNode
  theme: Theme
  onToggleTheme: () => void
  children: ReactNode
}) {
  return (
    <main className="auth-page">
      <header className="auth-header">
        <Link to="/" className="auth-brand" aria-label="SnapIt home">
          <span className="brand-icon" aria-hidden="true">S</span>
          <span>snapit</span>
        </Link>
        <button
          className="theme-toggle"
          type="button"
          onClick={onToggleTheme}
          aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
          title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
        >
          <span aria-hidden="true">{theme === 'dark' ? '☼' : '☾'}</span>
        </button>
      </header>

      <section className="auth-card" aria-labelledby="auth-title">
        <p className="eyebrow">{eyebrow}</p>
        <h1 id="auth-title">{title}</h1>
        <p className="auth-description">{description}</p>
        <div className="auth-form-content">{children}</div>
        <div className="auth-card-footer">{footer}</div>
      </section>

      <p className="auth-privacy-note">
        Your personal knowledge space, thoughtfully made.
      </p>
    </main>
  )
}

function FormField({
  id,
  label,
  type = 'text',
  value,
  onChange,
  autoComplete,
  error,
  minLength,
  maxLength,
  placeholder,
}: {
  id: string
  label: string
  type?: string
  value: string
  onChange: (value: string) => void
  autoComplete?: string
  error?: string
  minLength?: number
  maxLength?: number
  placeholder?: string
}) {
  return (
    <div className="auth-field">
      <label htmlFor={id}>{label}</label>
      <input
        id={id}
        name={id}
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        autoComplete={autoComplete}
        minLength={minLength}
        maxLength={maxLength}
        placeholder={placeholder}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : undefined}
      />
      {error && <p className="field-error" id={`${id}-error`}>{error}</p>}
    </div>
  )
}

function PasswordField({
  id,
  label,
  value,
  onChange,
  autoComplete,
  error,
}: {
  id: string
  label: string
  value: string
  onChange: (value: string) => void
  autoComplete: string
  error?: string
}) {
  const [visible, setVisible] = useState(false)

  return (
    <div className="auth-field">
      <label htmlFor={id}>{label}</label>
      <div className="password-input-wrap">
        <input
          id={id}
          name={id}
          type={visible ? 'text' : 'password'}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          autoComplete={autoComplete}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${id}-error` : undefined}
        />
        <button
          className="password-visibility"
          type="button"
          onClick={() => setVisible((isVisible) => !isVisible)}
          aria-label={visible ? 'Hide password' : 'Show password'}
          aria-pressed={visible}
        >
          {visible ? 'Hide' : 'Show'}
        </button>
      </div>
      {error && <p className="field-error" id={`${id}-error`}>{error}</p>}
    </div>
  )
}

function AuthNotice({ children }: { children: ReactNode }) {
  return <p className="auth-notice" role="status">{children}</p>
}

function LoginPage({ theme, onToggleTheme }: Omit<AuthPagesProps, 'page'>) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)
  const [errors, setErrors] = useState<FormErrors>({})
  const [notice, setNotice] = useState('')

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const nextErrors: FormErrors = {}

    if (!emailPattern.test(email.trim())) {
      nextErrors.email = 'Enter a valid email address.'
    }
    if (!password) {
      nextErrors.password = 'Enter your password.'
    }

    setErrors(nextErrors)
    setNotice('')
    if (Object.keys(nextErrors).length === 0) {
      setNotice('Sign-in is not connected yet. Your credentials were not sent.')
    }
  }

  return (
    <AuthLayout
      eyebrow="WELCOME BACK"
      title="Your library awaits."
      description="Sign in to pick up where you left off."
      theme={theme}
      onToggleTheme={onToggleTheme}
      footer={<>New to SnapIt? <Link to="/signup">Create an account</Link></>}
    >
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <FormField
          id="login-email"
          label="Email"
          type="email"
          value={email}
          onChange={setEmail}
          autoComplete="email"
          error={errors.email}
          placeholder="you@example.com"
        />
        <PasswordField
          id="login-password"
          label="Password"
          value={password}
          onChange={setPassword}
          autoComplete="current-password"
          error={errors.password}
        />
        <div className="auth-form-options">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(event) => setRememberMe(event.target.checked)}
            />
            <span>Remember me</span>
          </label>
          <Link to="/forgot-password">Forgot password?</Link>
        </div>
        <button className="button button-primary auth-submit" type="submit">Log in</button>
        {notice && <AuthNotice>{notice}</AuthNotice>}
      </form>
    </AuthLayout>
  )
}

function SignupPage({ theme, onToggleTheme }: Omit<AuthPagesProps, 'page'>) {
  const [fullName, setFullName] = useState('')
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [acceptedTerms, setAcceptedTerms] = useState(false)
  const [errors, setErrors] = useState<FormErrors>({})
  const [notice, setNotice] = useState('')

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const nextErrors: FormErrors = {}

    if (!fullName.trim()) {
      nextErrors['signup-name'] = 'Enter your name.'
    }
    if (!/^[a-zA-Z0-9_]{3,20}$/.test(username)) {
      nextErrors['signup-username'] = 'Use 3–20 letters, numbers, or underscores.'
    }
    if (!emailPattern.test(email.trim())) {
      nextErrors['signup-email'] = 'Enter a valid email address.'
    }
    if (password.length < 8) {
      nextErrors['signup-password'] = 'Use at least 8 characters.'
    }
    if (!confirmPassword) {
      nextErrors['signup-confirm-password'] = 'Confirm your password.'
    } else if (confirmPassword !== password) {
      nextErrors['signup-confirm-password'] = 'Passwords do not match.'
    }
    if (!acceptedTerms) {
      nextErrors['signup-terms'] = 'Accept the terms to continue.'
    }

    setErrors(nextErrors)
    setNotice('')
    if (Object.keys(nextErrors).length === 0) {
      setNotice('Account creation is not connected yet. Your details were not sent or saved.')
    }
  }

  return (
    <AuthLayout
      eyebrow="A PLACE FOR WHAT MATTERS"
      title="Make it yours."
      description="Create an account for your personal knowledge space."
      theme={theme}
      onToggleTheme={onToggleTheme}
      footer={<>Already have an account? <Link to="/login">Log in</Link></>}
    >
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <FormField
          id="signup-name"
          label="Full name"
          value={fullName}
          onChange={setFullName}
          autoComplete="name"
          maxLength={100}
          error={errors['signup-name']}
        />
        <FormField
          id="signup-username"
          label="Username"
          value={username}
          onChange={setUsername}
          autoComplete="username"
          minLength={3}
          maxLength={20}
          error={errors['signup-username']}
        />
        <FormField
          id="signup-email"
          label="Email"
          type="email"
          value={email}
          onChange={setEmail}
          autoComplete="email"
          placeholder="you@example.com"
          error={errors['signup-email']}
        />
        <PasswordField
          id="signup-password"
          label="Password"
          value={password}
          onChange={setPassword}
          autoComplete="new-password"
          error={errors['signup-password']}
        />
        <PasswordField
          id="signup-confirm-password"
          label="Confirm password"
          value={confirmPassword}
          onChange={setConfirmPassword}
          autoComplete="new-password"
          error={errors['signup-confirm-password']}
        />
        <div className="terms-field">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={acceptedTerms}
              onChange={(event) => setAcceptedTerms(event.target.checked)}
              aria-invalid={Boolean(errors['signup-terms'])}
              aria-describedby={errors['signup-terms'] ? 'signup-terms-error' : undefined}
            />
            <span>I agree to the terms and privacy policy.</span>
          </label>
          {errors['signup-terms'] && (
            <p className="field-error" id="signup-terms-error">{errors['signup-terms']}</p>
          )}
        </div>
        <button className="button button-primary auth-submit" type="submit">Create account</button>
        {notice && <AuthNotice>{notice}</AuthNotice>}
      </form>
    </AuthLayout>
  )
}

function ForgotPasswordPage({ theme, onToggleTheme }: Omit<AuthPagesProps, 'page'>) {
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [submitted, setSubmitted] = useState(false)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!emailPattern.test(email.trim())) {
      setError('Enter a valid email address.')
      setSubmitted(false)
      return
    }

    setError('')
    setSubmitted(true)
  }

  return (
    <AuthLayout
      eyebrow="ACCOUNT RECOVERY"
      title="Forgot your password?"
      description="Enter the email address associated with your account."
      theme={theme}
      onToggleTheme={onToggleTheme}
      footer={<>Remembered it? <Link to="/login">Back to log in</Link></>}
    >
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <FormField
          id="recovery-email"
          label="Email"
          type="email"
          value={email}
          onChange={(value) => {
            setEmail(value)
            setSubmitted(false)
          }}
          autoComplete="email"
          error={error}
          placeholder="you@example.com"
        />
        <button className="button button-primary auth-submit" type="submit">Continue</button>
        {submitted && (
          <AuthNotice>
            Password-reset email isn’t available yet. No email was sent.
          </AuthNotice>
        )}
      </form>
    </AuthLayout>
  )
}

function AuthPages({ page, theme, onToggleTheme }: AuthPagesProps) {
  if (page === 'login') {
    return <LoginPage theme={theme} onToggleTheme={onToggleTheme} />
  }
  if (page === 'signup') {
    return <SignupPage theme={theme} onToggleTheme={onToggleTheme} />
  }
  return <ForgotPasswordPage theme={theme} onToggleTheme={onToggleTheme} />
}

export default AuthPages
