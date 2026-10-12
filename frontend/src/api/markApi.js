import api from './axios';

export const markApi = {
  saveMark: async (data) => {
    const res = await api.post('/marks', data);
    return res.data;
  },
  saveBatch: async (data) => {
    const res = await api.post('/marks/batch', data);
    return res.data;
  },
  getByCourse: async (courseId, examType) => {
    const res = await api.get(`/marks/course/${courseId}`, { params: { examType } });
    return res.data;
  },
  getByEnrollment: async (enrollmentId) => {
    const res = await api.get(`/marks/enrollment/${enrollmentId}`);
    return res.data;
  },
  getMyMarks: async () => {
    const res = await api.get('/marks/my-marks');
    return res.data;
  },
};
