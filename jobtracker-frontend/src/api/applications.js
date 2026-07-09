import api from './axios'

export const getApplications = (page = 0, size = 50) =>
  api.get(`/applications?page=${page}&size=${size}`)

export const getApplication = (id) =>
  api.get(`/applications/${id}`)

export const createApplication = (data) =>
  api.post('/applications', data)

export const updateApplication = (id, data) =>
  api.put(`/applications/${id}`, data)

export const deleteApplication = (id) =>
  api.delete(`/applications/${id}`)

export const getStats = () =>
  api.get('/stats')