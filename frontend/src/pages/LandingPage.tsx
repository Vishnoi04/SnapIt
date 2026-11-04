import { useEffect, useState } from 'react'
import Navbar from '../components/layout/Navbar'
import '../App.css'

type Theme = 'dark' | 'light'

const features = [
  {
    number: '01',
    title: 'Save anything',
    description: 'Keep webpages, images, PDFs, notes, and useful code together.',
    status: 'Everyday essentials',
  },
  {
    number: '02',
    title: 'Find it faster',
    description: 'Search your personal library instead of retracing your steps.',
    status: 'Search that makes sense',
  },
  {
    number: '03',
    title: 'Build collections',
    description: 'Group related ideas into spaces that match the way you think.',
    status: 'Your own structure',
  },
  {
    number: '04',
    title: 'Add useful context',
    description: 'Use tags to connect references across projects and topics.',
    status: 'Flexible organization',
  },
  {
    number: '05',
    title: 'Read text in images',
    description: 'OCR and text extraction are planned for a future release.',
    status: 'Planned capability',
    planned: true,
  },
  {
    number: '06',
    title: 'Keep code close',
    description: 'Save snippets with their notes, source, and surrounding context.',
    status: 'Made for makers',
  },
  {
    number: '07',
    title: 'Return at the right time',
    description: 'Optional reminders are planned to help resurface saved ideas.',
    status: 'Planned capability',
    planned: true,
  },
  {
    number: '08',
    title: 'Share what matters',
    description: 'Sharing and collaboration are on the roadmap, not active yet.',
    status: 'Planned capability',
    planned: true,
  },
]

const faqs = [
  {
    question: 'What is SnapIt?',
    answer:
      'SnapIt is a home for the useful things you find online and create yourself, so they are easier to organize and revisit.',
  },
  {
    question: 'What can I save?',
    answer:
      'The product is designed for webpages, images and screenshots, PDFs, text notes, and code snippets.',
  },
  {
    question: 'Is SnapIt free?',
    answer:
      'A free plan is planned. Final plan details and pricing have not been announced yet.',
  },
  {
    question: 'Can I save PDFs?',
    answer:
      'Yes. PDF saving is part of the planned core experience, alongside webpages, images, notes, and code.',
  },
  {
    question: 'Can I save code snippets?',
    answer:
      'Yes. SnapIt is designed to keep code snippets with the context and tags that make them useful later.',
  },
  {
    question: 'How does smart search work?',
    answer:
      'Basic search is part of the product direction. Semantic search is a planned capability and is not connected yet.',
  },
  {
    question: 'Is my data private?',
    answer:
      'SnapIt is being built as a personal knowledge space. Account, storage, and privacy details will be shared as the service becomes available.',
  },
]

function readSavedTheme(): Theme {
  const savedTheme = window.localStorage.getItem('snapit-theme')
  return savedTheme === 'light' ? 'light' : 'dark'
}

function LandingPage() {
  const [theme, setTheme] = useState<Theme>(readSavedTheme)

  useEffect(() => {
    window.localStorage.setItem('snapit-theme', theme)
  }, [theme])

  return (
    <div className="landing-page" data-theme={theme}>
      <Navbar
        theme={theme}
        onToggleTheme={() =>
          setTheme((currentTheme) => (currentTheme === 'dark' ? 'light' : 'dark'))
        }
      />

      <main>
        <section className="landing-hero" aria-labelledby="hero-title">
          <div className="hero-copy">
            <p className="eyebrow"><span className="eyebrow-dot" /> YOUR PERSONAL KNOWLEDGE SPACE</p>
            <h1 id="hero-title">
              Save anything.
              <br />
              <span>Find it instantly.</span>
            </h1>
            <p className="hero-description">
              Websites, images, PDFs, notes, and code — everything worth
              keeping, all in one beautiful space.
            </p>
            <div className="hero-actions">
              <a href="/signup" className="button button-primary">
                Start for free <span aria-hidden="true">↗</span>
              </a>
              <a href="#features" className="button button-secondary">
                Explore features <span aria-hidden="true">↓</span>
              </a>
            </div>
            <p className="hero-note">A calmer place for everything you want to remember.</p>
          </div>

          <ProductPreview />
          <a className="scroll-cue" href="#why-snapit">
            <span aria-hidden="true">↓</span> A better way to keep what matters
          </a>
        </section>

        <section className="section why-section" id="why-snapit">
          <div className="section-intro">
            <p className="eyebrow">THE LOST-IN-TABS PROBLEM</p>
            <h2>Good ideas deserve<br />a better place to live.</h2>
          </div>
          <div className="why-copy">
            <p>
              The article you meant to read. The screenshot you know you saved.
              The snippet buried in a chat. Useful things end up everywhere.
            </p>
            <p>
              SnapIt brings your references into one thoughtful library, ready
              to be organized and found when you need them.
            </p>
            <a className="text-link" href="#how-it-works">
              See how it works <span aria-hidden="true">↗</span>
            </a>
          </div>
        </section>

        <section className="section features-section" id="features">
          <div className="section-heading">
            <div>
              <p className="eyebrow">A HOME FOR YOUR CURIOSITY</p>
              <h2>Everything worth keeping,<br />all in one place.</h2>
            </div>
            <p>
              From a passing spark to a well-organized reference library,
              SnapIt helps your knowledge grow with you.
            </p>
          </div>
          <div className="feature-grid">
            {features.map((feature) => (
              <article className="feature-card" key={feature.number}>
                <div className="feature-card-top">
                  <span className="feature-number">{feature.number}</span>
                  {feature.planned && <span className="planned-label">Planned</span>}
                </div>
                <h3>{feature.title}</h3>
                <p>{feature.description}</p>
                <span className="feature-status">{feature.status}</span>
              </article>
            ))}
          </div>
        </section>

        <section className="section how-section" id="how-it-works">
          <div className="how-heading">
            <p className="eyebrow">THREE SIMPLE STEPS</p>
            <h2>From “save this”<br />to “there it is.”</h2>
          </div>
          <ol className="steps-list">
            <li>
              <span className="step-number">01</span>
              <div><h3>Capture what matters</h3><p>Bring useful webpages, images, documents, notes, and code into SnapIt.</p></div>
            </li>
            <li>
              <span className="step-number">02</span>
              <div><h3>Make it your own</h3><p>Add a note, choose a collection, or tag it in a way that feels natural.</p></div>
            </li>
            <li>
              <span className="step-number">03</span>
              <div><h3>Find it when you need it</h3><p>Search your library and get back to the ideas you saved for later.</p></div>
            </li>
          </ol>
        </section>

        <section className="section library-section" aria-labelledby="library-title">
          <div className="library-copy">
            <p className="eyebrow">YOUR KNOWLEDGE, IN VIEW</p>
            <h2 id="library-title">A library that feels like yours.</h2>
            <p>
              Keep different kinds of references together without losing what
              makes each one useful. A visual home for the things you collect.
            </p>
            <a className="text-link" href="/signup">
              Make room for your ideas <span aria-hidden="true">↗</span>
            </a>
          </div>
          <ProductPreview compact />
        </section>

        <section className="section pricing-section" id="pricing">
          <div className="pricing-heading">
            <p className="eyebrow">SIMPLE PLANS, WHEN THEY’RE READY</p>
            <h2>Start with what you need.</h2>
            <p>Plan details and final pricing are still being decided.</p>
          </div>
          <div className="pricing-grid">
            <article className="plan-card">
              <p className="plan-label">FREE</p>
              <h3>A thoughtful place to start.</h3>
              <p className="plan-price">Free <span>— planned</span></p>
              <ul>
                <li>Save webpages, images, and PDFs</li>
                <li>Keep notes and code snippets</li>
                <li>Basic collections and tags</li>
                <li>Personal dashboard and basic search</li>
              </ul>
              <a className="button button-secondary" href="/signup">Get started</a>
            </article>
            <article className="plan-card plan-card-premium">
              <span className="premium-badge">A LITTLE MORE ROOM</span>
              <p className="plan-label">PREMIUM</p>
              <h3>For a library that keeps growing.</h3>
              <p className="plan-price">Coming soon</p>
              <ul>
                <li>Everything in Free</li>
                <li>Higher storage limits</li>
                <li>Advanced organization and search</li>
                <li>Future OCR and semantic search features</li>
              </ul>
              <a className="button button-premium" href="#faq">
                Plan details soon <span aria-hidden="true">↓</span>
              </a>
            </article>
          </div>
        </section>

        <section className="section faq-section" id="faq">
          <div className="faq-heading">
            <p className="eyebrow">GOOD QUESTIONS</p>
            <h2>A few things<br />you might wonder.</h2>
          </div>
          <div className="faq-list">
            {faqs.map((faq) => (
              <details key={faq.question}>
                <summary>{faq.question}<span aria-hidden="true">+</span></summary>
                <p>{faq.answer}</p>
              </details>
            ))}
          </div>
        </section>

        <section className="closing-cta">
          <p className="eyebrow">MAKE SPACE FOR YOUR NEXT BIG IDEA</p>
          <h2>Keep the things<br />you’ll want to find.</h2>
          <a className="button button-primary" href="/signup">
            Get started with SnapIt <span aria-hidden="true">↗</span>
          </a>
        </section>
      </main>

      <footer className="site-footer">
        <a href="/" className="footer-brand" aria-label="SnapIt home">
          <span className="brand-icon" aria-hidden="true">S</span>
          <span>snapit</span>
        </a>
        <p className="footer-note">A little more room for what matters.</p>
        <div className="footer-links">
          <div><span>PRODUCT</span><a href="#features">Features</a><a href="#how-it-works">How it works</a><a href="#pricing">Pricing</a></div>
          <div><span>COMPANY</span><a href="#why-snapit">About SnapIt</a><a href="#faq">FAQ</a></div>
          <div><span>LEGAL</span><span>Privacy · coming soon</span><span>Terms · coming soon</span></div>
        </div>
        <p className="copyright">© {new Date().getFullYear()} SnapIt. Keep what matters.</p>
      </footer>
    </div>
  )
}

function ProductPreview({ compact = false }: { compact?: boolean }) {
  return (
    <div
      className={`product-preview${compact ? ' product-preview-compact' : ''}`}
      role="img"
      aria-label="Preview of the SnapIt saved-items dashboard"
    >
      <div className="preview-window">
        <div className="preview-sidebar">
          <div className="preview-brand"><span className="brand-icon">S</span><span>snapit</span></div>
          <span className="preview-nav-active">◫ &nbsp; All items</span>
          <span>♡ &nbsp; Favorites</span>
          <span>▧ &nbsp; Collections</span>
          <div className="preview-collection-label">YOUR COLLECTIONS</div>
          <span>✳ &nbsp; Design ideas</span>
          <span>⌘ &nbsp; Work notes</span>
        </div>
        <div className="preview-main">
          <div className="preview-topbar">
            <span>Tuesday, November 4</span>
            <span className="preview-avatar">V</span>
          </div>
          <div className="preview-welcome">
            <span>YOUR LIBRARY</span>
            <strong>A good place to think.</strong>
          </div>
          <div className="preview-search">⌕ &nbsp; Search your saved items...</div>
          <div className="preview-filter-row"><span className="preview-filter-selected">All items</span><span>Websites</span><span>Images</span><span>Notes</span></div>
          <div className="preview-items">
            <article className="preview-item preview-image-item">
              <div className="preview-art"><span>FORM<br />& FUNCTION</span><i /></div>
              <div className="preview-item-copy"><span>IMAGE · 2 DAYS AGO</span><strong>Quiet spaces, considered</strong></div>
            </article>
            <article className="preview-item">
              <div className="preview-note-art"><span>“</span><p>Collect moments<br />of clarity.</p></div>
              <div className="preview-item-copy"><span>NOTE · YESTERDAY</span><strong>Thoughts on focus</strong></div>
            </article>
            <article className="preview-item preview-web-item">
              <div className="preview-web-art"><span>● &nbsp; fieldnotes.design</span><strong>On making<br />with intention</strong><small>fieldnotes.design/journal</small></div>
              <div className="preview-item-copy"><span>WEBSITE · MONDAY</span><strong>Studio Field Notes</strong></div>
            </article>
          </div>
          <div className="preview-footer"><span>12 items in your library</span><span>Made for curious minds</span></div>
        </div>
      </div>
      {!compact && <div className="preview-callout"><span className="callout-spark">✳</span><span><strong>Everything, together.</strong><br />One lovely place to pick up where you left off.</span></div>}
    </div>
  )
}

export default LandingPage
