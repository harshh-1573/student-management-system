import api from './axios';

export const attendanceApi = {
  markBatch: async (data) => {
    const res = await api.post('/attendance/batch', data);
    return res.data;
  },
  getByCourseAndDate: async (courseId, date) => {
    const res = await api.get(`/attendance/course/${courseId}`, { params: { date } });
    return res.data;
  },
  getCourseStats: async (courseId) => {
    const res = await api.get(`/attendance/course/${courseId}/stats`);
    return res.data;
  },
  getMyAttendance: async () => {
    const res = await api.get('/attendance/my-attendance');
    return res.data;
  },
};
