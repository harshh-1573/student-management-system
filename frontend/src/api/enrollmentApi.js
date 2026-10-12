import api from './axios';

export const enrollmentApi = {
  enroll: async (data) => {
    const res = await api.post('/enrollments', data);
    return res.data;
  },
  drop: async (id) => {
    const res = await api.delete(`/enrollments/${id}`);
    return res.data;
  },
  getByCourse: async (courseId, params) => {
    const res = await api.get(`/enrollments/course/${courseId}`, { params });
    return res.data;
  },
  getByStudent: async (studentId, params) => {
    const res = await api.get(`/enrollments/student/${studentId}`, { params });
    return res.data;
  },
  getMyEnrollments: async () => {
    const res = await api.get('/enrollments/my-enrollments');
    return res.data;
  },
};
