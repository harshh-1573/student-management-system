import api from './axios';

export const courseApi = {
  getAll: async (params) => {
    const res = await api.get('/courses', { params });
    return res.data;
  },
  getAllList: async () => {
    const res = await api.get('/courses/all');
    return res.data;
  },
  getById: async (id) => {
    const res = await api.get(`/courses/${id}`);
    return res.data;
  },
  getMyCourses: async () => {
    const res = await api.get('/courses/my-courses');
    return res.data;
  },
  create: async (data) => {
    const res = await api.post('/courses', data);
    return res.data;
  },
  update: async (id, data) => {
    const res = await api.put(`/courses/${id}`, data);
    return res.data;
  },
  delete: async (id) => {
    const res = await api.delete(`/courses/${id}`);
    return res.data;
  },
};
