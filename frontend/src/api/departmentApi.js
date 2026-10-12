import api from './axios';

export const departmentApi = {
  getAll: async (params) => {
    const res = await api.get('/departments', { params });
    return res.data;
  },
  getAllList: async () => {
    const res = await api.get('/departments/all');
    return res.data;
  },
  getById: async (id) => {
    const res = await api.get(`/departments/${id}`);
    return res.data;
  },
  create: async (data) => {
    const res = await api.post('/departments', data);
    return res.data;
  },
  update: async (id, data) => {
    const res = await api.put(`/departments/${id}`, data);
    return res.data;
  },
  delete: async (id) => {
    const res = await api.delete(`/departments/${id}`);
    return res.data;
  },
};
