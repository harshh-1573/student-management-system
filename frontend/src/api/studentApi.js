import api from './axios';

export const studentApi = {
  getAll: async (params) => {
    const res = await api.get('/students', { params });
    return res.data;
  },
  getAllList: async () => {
    const res = await api.get('/students/all');
    return res.data;
  },
  getById: async (id) => {
    const res = await api.get(`/students/${id}`);
    return res.data;
  },
  getMe: async () => {
    const res = await api.get('/students/me');
    return res.data;
  },
  getReport: async (id) => {
    const res = await api.get(`/students/${id}/report`);
    return res.data;
  },
  getMyReport: async () => {
    const res = await api.get('/students/me/report');
    return res.data;
  },
  create: async (data) => {
    const res = await api.post('/students', data);
    return res.data;
  },
  update: async (id, data) => {
    const res = await api.put(`/students/${id}`, data);
    return res.data;
  },
  delete: async (id) => {
    const res = await api.delete(`/students/${id}`);
    return res.data;
  },
};
