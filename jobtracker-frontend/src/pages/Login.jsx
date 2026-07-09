import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { login } from '../api/auth'

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login: authLogin } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await login(form)
      const token = res.data?.token
      if (!token) {
        throw new Error('No authentication token received')
      }

      authLogin(
        { name: res.data?.name || form.email, userId: res.data?.userId },
        token
      )
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>Job Tracker</h1>
        <p style={styles.subtitle}>Sign in to your account</p>
        {error && <div style={styles.error}>{error}</div>}
        <form onSubmit={handleSubmit}>
          <div style={styles.field}>
            <label style={styles.label}>Email</label>
            <input
              style={styles.input}
              type="email"
              placeholder="ahmad@gmail.com"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              required
            />
          </div>
          <div style={styles.field}>
            <label style={styles.label}>Password</label>
            <input
              style={styles.input}
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={e => setForm({ ...form, password: e.target.value })}
              required
            />
          </div>
          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <p style={styles.link}>
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  )
}

const styles = {
  container: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f0f2f5' },
  card: { background: 'white', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px' },
  title: { fontSize: '1.8rem', fontWeight: '700', color: '#1a1a2e', marginBottom: '0.25rem' },
  subtitle: { color: '#666', marginBottom: '1.5rem' },
  field: { marginBottom: '1rem' },
  label: { display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#444', marginBottom: '0.4rem' },
  input: { width: '100%', padding: '0.75rem', border: '1px solid #ddd', borderRadius: '8px', fontSize: '0.95rem' },
  button: { width: '100%', padding: '0.875rem', background: '#4f46e5', color: 'white', borderRadius: '8px', fontSize: '1rem', fontWeight: '600', marginTop: '0.5rem' },
  error: { background: '#fee2e2', color: '#dc2626', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' },
  link: { textAlign: 'center', marginTop: '1.5rem', color: '#666', fontSize: '0.875rem' },
}