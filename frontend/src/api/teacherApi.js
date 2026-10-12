import api from './axios';

export const teacherApi = {
  getAll: async (params) => {
    const res = await api.get('/teachers', { params });
    return res.data;
  },
  getAllList: async () => {
    const res = await api.get('/teachers/all');
    return res.data;
  },
  getById: async (id) => {
    const res = await api.get(`/teachers/${id}`);
    return res.data;
  },
  getMe: async () => {
    const res = await api.get('/teachers/me');
    return res.data;
  },
  create: async (data) => {
    const res = await api.post('/teachers', data);
    return res.data;
  },
  update: async (id, data) => {
    const res = await api.put(`/teachers/${id}`, data);
    return res.data;
  },
  delete: async (id) => {
    const res = await api.delete(`/teachers/${id}`);
    return res.data;
  },
};
