import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getApplications, createApplication, updateApplication, deleteApplication } from '../api/applications'

const STATUSES = ['APPLIED', 'INTERVIEWING', 'OFFERED', 'REJECTED', 'WITHDRAWN']

const STATUS_COLORS = {
  APPLIED: { bg: '#dbeafe', border: '#3b82f6', text: '#1d4ed8', header: '#3b82f6' },
  INTERVIEWING: { bg: '#fef3c7', border: '#f59e0b', text: '#d97706', header: '#f59e0b' },
  OFFERED: { bg: '#d1fae5', border: '#10b981', text: '#065f46', header: '#10b981' },
  REJECTED: { bg: '#fee2e2', border: '#ef4444', text: '#dc2626', header: '#ef4444' },
  WITHDRAWN: { bg: '#f3f4f6', border: '#9ca3af', text: '#6b7280', header: '#9ca3af' },
}

export default function KanbanBoard() {
  const { logout } = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showModal, setShowModal] = useState(false)
  const [draggedId, setDraggedId] = useState(null)
  const [form, setForm] = useState({
    companyName: '', jobTitle: '', jobUrl: '',
    status: 'APPLIED', priority: 'MEDIUM',
    jobType: 'FULL_TIME', experienceRequired: 'FRESH',
    workType: 'REMOTE', domain: '', appliedDate: new Date().toISOString().split('T')[0],
    notes: ''
  })

  const { data, isLoading } = useQuery({
    queryKey: ['applications'],
    queryFn: () => getApplications(0, 100).then(r => r.data.content),
  })

  const createMutation = useMutation({
    mutationFn: createApplication,
    onSuccess: () => {
      queryClient.invalidateQueries(['applications'])
      queryClient.invalidateQueries(['stats'])
      setShowModal(false)
      resetForm()
    }
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => updateApplication(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['applications'])
      queryClient.invalidateQueries(['stats'])
    }
  })

  const deleteMutation = useMutation({
    mutationFn: deleteApplication,
    onSuccess: () => {
      queryClient.invalidateQueries(['applications'])
      queryClient.invalidateQueries(['stats'])
    }
  })

  const resetForm = () => setForm({
    companyName: '', jobTitle: '', jobUrl: '',
    status: 'APPLIED', priority: 'MEDIUM',
    jobType: 'FULL_TIME', experienceRequired: 'FRESH',
    workType: 'REMOTE', domain: '', appliedDate: new Date().toISOString().split('T')[0],
    notes: ''
  })

  const handleDrop = (status) => {
    if (draggedId) {
      updateMutation.mutate({ id: draggedId, data: { status } })
      setDraggedId(null)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    createMutation.mutate(form)
  }

  const getByStatus = (status) =>
    (data || []).filter(app => app.status === status)

  return (
    <div style={styles.container}>
      <nav style={styles.nav}>
        <h1 style={styles.logo}>Job Tracker</h1>
        <div style={styles.navRight}>
          <button style={styles.navBtn} onClick={() => navigate('/')}>Dashboard</button>
          <button style={styles.addBtn} onClick={() => setShowModal(true)}>+ Add Application</button>
          <button style={styles.logoutBtn} onClick={() => { logout(); navigate('/login') }}>Logout</button>
        </div>
      </nav>

      {isLoading ? (
        <div style={styles.loading}>Loading applications...</div>
      ) : (
        <div style={styles.board}>
          {STATUSES.map(status => (
            <div
              key={status}
              style={styles.column}
              onDragOver={e => e.preventDefault()}
              onDrop={() => handleDrop(status)}
            >
              <div style={{ ...styles.columnHeader, background: STATUS_COLORS[status].header }}>
                <span style={styles.columnTitle}>{status}</span>
                <span style={styles.columnCount}>{getByStatus(status).length}</span>
              </div>

              <div style={styles.cards}>
                {getByStatus(status).map(app => (
                  <div
                    key={app.id}
                    style={styles.card}
                    draggable
                    onDragStart={() => setDraggedId(app.id)}
                  >
                    <div style={styles.cardTop}>
                      <h3 style={styles.company}>{app.companyName}</h3>
                      <button
                        style={styles.deleteBtn}
                        onClick={() => deleteMutation.mutate(app.id)}
                      >×</button>
                    </div>
                    <p style={styles.jobTitle}>{app.jobTitle}</p>
                    <div style={styles.cardMeta}>
                      <span style={styles.badge}>{app.priority}</span>
                      <span style={styles.badge}>{app.workType}</span>
                    </div>
                    <p style={styles.date}>{app.appliedDate}</p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <div style={styles.overlay}>
          <div style={styles.modal}>
            <div style={styles.modalHeader}>
              <h2 style={styles.modalTitle}>Add Application</h2>
              <button style={styles.closeBtn} onClick={() => { setShowModal(false); resetForm() }}>×</button>
            </div>
            <form onSubmit={handleSubmit} style={styles.form}>
              <div style={styles.formGrid}>
                <div style={styles.field}>
                  <label style={styles.label}>Company Name *</label>
                  <input style={styles.input} value={form.companyName}
                    onChange={e => setForm({ ...form, companyName: e.target.value })} required />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Job Title *</label>
                  <input style={styles.input} value={form.jobTitle}
                    onChange={e => setForm({ ...form, jobTitle: e.target.value })} required />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Job URL</label>
                  <input style={styles.input} value={form.jobUrl}
                    onChange={e => setForm({ ...form, jobUrl: e.target.value })} />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Applied Date *</label>
                  <input style={styles.input} type="date" value={form.appliedDate}
                    onChange={e => setForm({ ...form, appliedDate: e.target.value })} required />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Status</label>
                  <select style={styles.input} value={form.status}
                    onChange={e => setForm({ ...form, status: e.target.value })}>
                    {STATUSES.map(s => <option key={s}>{s}</option>)}
                  </select>
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Priority</label>
                  <select style={styles.input} value={form.priority}
                    onChange={e => setForm({ ...form, priority: e.target.value })}>
                    {['LOW', 'MEDIUM', 'HIGH'].map(p => <option key={p}>{p}</option>)}
                  </select>
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Job Type</label>
                  <select style={styles.input} value={form.jobType}
                    onChange={e => setForm({ ...form, jobType: e.target.value })}>
                    {['FULL_TIME', 'PART_TIME', 'CONTRACT'].map(t => <option key={t}>{t}</option>)}
                  </select>
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Work Type</label>
                  <select style={styles.input} value={form.workType}
                    onChange={e => setForm({ ...form, workType: e.target.value })}>
                    {['REMOTE', 'HYBRID', 'ONSITE'].map(w => <option key={w}>{w}</option>)}
                  </select>
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Experience Required</label>
                  <select style={styles.input} value={form.experienceRequired}
                    onChange={e => setForm({ ...form, experienceRequired: e.target.value })}>
                    {['FRESH', 'ONE_PLUS', 'TWO_PLUS', 'FIVE_PLUS'].map(x => <option key={x}>{x}</option>)}
                  </select>
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>Domain</label>
                  <input style={styles.input} value={form.domain}
                    onChange={e => setForm({ ...form, domain: e.target.value })}
                    placeholder="Backend, DevOps..." />
                </div>
              </div>
              <div style={styles.field}>
                <label style={styles.label}>Notes</label>
                <textarea style={{ ...styles.input, height: '80px', resize: 'vertical' }}
                  value={form.notes}
                  onChange={e => setForm({ ...form, notes: e.target.value })} />
              </div>
              <div style={styles.modalFooter}>
                <button type="button" style={styles.cancelBtn}
                  onClick={() => { setShowModal(false); resetForm() }}>Cancel</button>
                <button type="submit" style={styles.submitBtn}
                  disabled={createMutation.isPending}>
                  {createMutation.isPending ? 'Adding...' : 'Add Application'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

const styles = {
  container: { minHeight: '100vh', background: '#f0f2f5' },
  nav: { background: 'white', padding: '1rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' },
  logo: { fontSize: '1.4rem', fontWeight: '700', color: '#4f46e5' },
  navRight: { display: 'flex', alignItems: 'center', gap: '1rem' },
  navBtn: { padding: '0.5rem 1rem', background: '#f3f4f6', color: '#374151', borderRadius: '8px', fontSize: '0.875rem', fontWeight: '500' },
  addBtn: { padding: '0.5rem 1rem', background: '#4f46e5', color: 'white', borderRadius: '8px', fontSize: '0.875rem', fontWeight: '600' },
  logoutBtn: { padding: '0.5rem 1rem', background: '#fee2e2', color: '#dc2626', borderRadius: '8px', fontSize: '0.875rem', fontWeight: '500' },
  loading: { display: 'flex', justifyContent: 'center', padding: '4rem', fontSize: '1.1rem', color: '#666' },
  board: { display: 'flex', gap: '1rem', padding: '1.5rem', overflowX: 'auto', minHeight: 'calc(100vh - 70px)' },
  column: { minWidth: '260px', flex: '1', background: 'white', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)', display: 'flex', flexDirection: 'column', maxHeight: 'calc(100vh - 100px)' },
  columnHeader: { padding: '1rem', borderRadius: '12px 12px 0 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  columnTitle: { color: 'white', fontWeight: '700', fontSize: '0.875rem' },
  columnCount: { background: 'rgba(255,255,255,0.3)', color: 'white', borderRadius: '20px', padding: '0.15rem 0.6rem', fontSize: '0.8rem', fontWeight: '600' },
  cards: { padding: '0.75rem', display: 'flex', flexDirection: 'column', gap: '0.75rem', overflowY: 'auto', flex: 1 },
  card: { background: '#fafafa', border: '1px solid #e5e7eb', borderRadius: '10px', padding: '1rem', cursor: 'grab', transition: 'box-shadow 0.2s' },
  cardTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.25rem' },
  company: { fontSize: '0.95rem', fontWeight: '700', color: '#1a1a2e' },
  jobTitle: { fontSize: '0.825rem', color: '#666', marginBottom: '0.5rem' },
  cardMeta: { display: 'flex', gap: '0.4rem', marginBottom: '0.4rem', flexWrap: 'wrap' },
  badge: { fontSize: '0.7rem', padding: '0.15rem 0.5rem', background: '#e0e7ff', color: '#4f46e5', borderRadius: '20px', fontWeight: '500' },
  date: { fontSize: '0.75rem', color: '#9ca3af' },
  deleteBtn: { background: 'none', color: '#9ca3af', fontSize: '1.2rem', lineHeight: 1, padding: '0 0.25rem', cursor: 'pointer' },
  overlay: { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem' },
  modal: { background: 'white', borderRadius: '16px', width: '100%', maxWidth: '700px', maxHeight: '90vh', overflow: 'auto', boxShadow: '0 20px 60px rgba(0,0,0,0.3)' },
  modalHeader: { padding: '1.5rem', borderBottom: '1px solid #e5e7eb', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  modalTitle: { fontSize: '1.25rem', fontWeight: '700', color: '#1a1a2e' },
  closeBtn: { background: 'none', fontSize: '1.5rem', color: '#9ca3af', cursor: 'pointer' },
  form: { padding: '1.5rem' },
  formGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' },
  field: { marginBottom: '0' },
  label: { display: 'block', fontSize: '0.8rem', fontWeight: '500', color: '#444', marginBottom: '0.3rem' },
  input: { width: '100%', padding: '0.625rem', border: '1px solid #ddd', borderRadius: '8px', fontSize: '0.875rem' },
  modalFooter: { display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' },
  cancelBtn: { padding: '0.625rem 1.25rem', background: '#f3f4f6', color: '#374151', borderRadius: '8px', fontWeight: '500' },
  submitBtn: { padding: '0.625rem 1.5rem', background: '#4f46e5', color: 'white', borderRadius: '8px', fontWeight: '600' },
}