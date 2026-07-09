import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getStats } from '../api/applications'

export default function Dashboard() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const { data: stats, isLoading } = useQuery({
    queryKey: ['stats'],
    queryFn: () => getStats().then(r => r.data),
  })

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div style={styles.container}>
      <nav style={styles.nav}>
        <h1 style={styles.logo}>Job Tracker</h1>
        <div style={styles.navRight}>
          <span style={styles.welcome}>Welcome, {user?.name}</span>
          <button style={styles.navBtn} onClick={() => navigate('/board')}>Kanban Board</button>
          <button style={styles.logoutBtn} onClick={handleLogout}>Logout</button>
        </div>
      </nav>

      <div style={styles.content}>
        <h2 style={styles.pageTitle}>Dashboard</h2>

        {isLoading ? (
          <p>Loading stats...</p>
        ) : (
          <>
            <div style={styles.statsGrid}>
              <div style={styles.statCard}>
                <div style={styles.statNumber}>{stats?.totalApplications || 0}</div>
                <div style={styles.statLabel}>Total Applications</div>
              </div>
              <div style={{ ...styles.statCard, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
                <div style={styles.statNumber}>{stats?.thisMonth || 0}</div>
                <div style={styles.statLabel}>This Month</div>
              </div>
              <div style={{ ...styles.statCard, background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }}>
                <div style={styles.statNumber}>{stats?.thisWeek || 0}</div>
                <div style={styles.statLabel}>This Week</div>
              </div>
              <div style={{ ...styles.statCard, background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }}>
                <div style={styles.statNumber}>{stats?.responseRate || 0}%</div>
                <div style={styles.statLabel}>Response Rate</div>
              </div>
            </div>

            <h3 style={styles.sectionTitle}>Applications by Status</h3>
            <div style={styles.statusGrid}>
              {stats?.byStatus && Object.entries(stats.byStatus).map(([status, count]) => (
                <div key={status} style={styles.statusCard}>
                  <span style={{ ...styles.statusBadge, ...getStatusStyle(status) }}>{status}</span>
                  <span style={styles.statusCount}>{count}</span>
                </div>
              ))}
            </div>
          </>
        )}

        <button style={styles.boardBtn} onClick={() => navigate('/board')}>
          View Kanban Board →
        </button>
      </div>
    </div>
  )
}

function getStatusStyle(status) {
  const colors = {
    APPLIED: { background: '#dbeafe', color: '#1d4ed8' },
    INTERVIEWING: { background: '#fef3c7', color: '#d97706' },
    OFFERED: { background: '#d1fae5', color: '#065f46' },
    REJECTED: { background: '#fee2e2', color: '#dc2626' },
    WITHDRAWN: { background: '#f3f4f6', color: '#6b7280' },
  }
  return colors[status] || { background: '#f3f4f6', color: '#6b7280' }
}

const styles = {
  container: { minHeight: '100vh', background: '#f0f2f5' },
  nav: { background: 'white', padding: '1rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
  logo: { fontSize: '1.4rem', fontWeight: '700', color: '#4f46e5' },
  navRight: { display: 'flex', alignItems: 'center', gap: '1rem' },
  welcome: { color: '#666', fontSize: '0.9rem' },
  navBtn: { padding: '0.5rem 1rem', background: '#4f46e5', color: 'white', borderRadius: '8px', fontSize: '0.875rem', fontWeight: '500' },
  logoutBtn: { padding: '0.5rem 1rem', background: '#fee2e2', color: '#dc2626', borderRadius: '8px', fontSize: '0.875rem', fontWeight: '500' },
  content: { padding: '2rem', maxWidth: '1200px', margin: '0 auto' },
  pageTitle: { fontSize: '1.75rem', fontWeight: '700', color: '#1a1a2e', marginBottom: '1.5rem' },
  statsGrid: { display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1rem', marginBottom: '2rem' },
  statCard: { background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)', borderRadius: '12px', padding: '1.5rem', color: 'white' },
  statNumber: { fontSize: '2.5rem', fontWeight: '700', marginBottom: '0.25rem' },
  statLabel: { fontSize: '0.875rem', opacity: 0.9 },
  sectionTitle: { fontSize: '1.1rem', fontWeight: '600', color: '#1a1a2e', marginBottom: '1rem' },
  statusGrid: { display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '1rem', marginBottom: '2rem' },
  statusCard: { background: 'white', borderRadius: '10px', padding: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' },
  statusBadge: { padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.75rem', fontWeight: '600', textAlign: 'center' },
  statusCount: { fontSize: '1.5rem', fontWeight: '700', textAlign: 'center', color: '#1a1a2e' },
  boardBtn: { padding: '0.875rem 2rem', background: '#4f46e5', color: 'white', borderRadius: '10px', fontSize: '1rem', fontWeight: '600' },
}