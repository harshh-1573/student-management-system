import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { courseApi } from '../../api/courseApi';
import { enrollmentApi } from '../../api/enrollmentApi';
import { attendanceApi } from '../../api/attendanceApi';
import Loader from '../../components/common/Loader';
import EmptyState from '../../components/common/EmptyState';
import { Calendar, CheckCircle2, XCircle, Clock, Save, BarChart3, Users, CheckCheck } from 'lucide-react';
import toast from 'react-hot-toast';

export default function AttendancePage() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [activeTab, setActiveTab] = useState('mark'); // 'mark' | 'stats'

  // Mark tab state
  const [students, setStudents] = useState([]); // from enrollments
  const [attendanceMap, setAttendanceMap] = useState({}); // studentId -> status (PRESENT, ABSENT, LATE)
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Stats tab state
  const [stats, setStats] = useState([]);
  const [statsLoading, setStatsLoading] = useState(false);

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    if (selectedCourseId) {
      if (activeTab === 'mark') {
        fetchRosterAndAttendance(selectedCourseId, date);
      } else {
        fetchStats(selectedCourseId);
      }
    }
  }, [selectedCourseId, date, activeTab]);

  const fetchCourses = async () => {
    try {
      const res = await courseApi.getAllList();
      const list = res.data || [];
      setCourses(list);
      if (list.length > 0) {
        setSelectedCourseId(list[0].id.toString());
      }
    } catch (err) {
      toast.error('Failed to load courses');
    }
  };

  const fetchRosterAndAttendance = async (courseId, selectedDate) => {
    setLoading(true);
    try {
      // 1. Get enrolled students
      const enrollRes = await enrollmentApi.getByCourse(courseId, { page: 0, size: 200 });
      const enrollments = enrollRes.data?.content || [];
      const activeEnrollments = enrollments.filter((e) => e.status === 'ACTIVE');
      setStudents(activeEnrollments);

      // 2. Get any existing attendance records for this date
      const attRes = await attendanceApi.getByCourseAndDate(courseId, selectedDate);
      const existingRecords = attRes.data || [];

      // Build initial map: default everyone to PRESENT if no existing record, else existing status
      const initialMap = {};
      activeEnrollments.forEach((e) => {
        const found = existingRecords.find((r) => r.studentId === e.studentId);
        initialMap[e.studentId] = found ? found.status : 'PRESENT';
      });
      setAttendanceMap(initialMap);
    } catch (err) {
      toast.error('Failed to load attendance sheet');
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async (courseId) => {
    setStatsLoading(true);
    try {
      const res = await attendanceApi.getCourseStats(courseId);
      setStats(res.data || []);
    } catch (err) {
      toast.error('Failed to load attendance statistics');
    } finally {
      setStatsLoading(false);
    }
  };

  const setAllStatus = (status) => {
    const updated = {};
    students.forEach((s) => {
      updated[s.studentId] = status;
    });
    setAttendanceMap(updated);
  };

  const handleStatusChange = (studentId, status) => {
    setAttendanceMap((prev) => ({
      ...prev,
      [studentId]: status,
    }));
  };

  const handleSaveAttendance = async () => {
    if (!selectedCourseId) return;
    if (students.length === 0) {
      toast.error('No enrolled students to mark attendance for');
      return;
    }

    setSaving(true);
    try {
      const records = students.map((s) => ({
        studentId: s.studentId,
        status: attendanceMap[s.studentId] || 'PRESENT',
      }));

      await attendanceApi.markBatch({
        courseId: Number(selectedCourseId),
        date: date,
        records: records,
      });

      toast.success('Attendance saved successfully');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save attendance';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const selectedCourse = courses.find((c) => c.id.toString() === selectedCourseId);

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 tracking-tight">Attendance Management</h1>
          <p className="text-sm text-slate-500 mt-1">
            Track daily class attendance and monitor student presence statistics
          </p>
        </div>

        {/* Tab switch */}
        <div className="flex bg-slate-100 p-1 rounded-xl self-start sm:self-auto">
          <button
            onClick={() => setActiveTab('mark')}
            className={`px-4 py-2 text-xs font-semibold rounded-lg transition-all flex items-center space-x-1.5 ${
              activeTab === 'mark'
                ? 'bg-white text-indigo-700 shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Calendar className="w-3.5 h-3.5" />
            <span>Mark Attendance</span>
          </button>
          <button
            onClick={() => setActiveTab('stats')}
            className={`px-4 py-2 text-xs font-semibold rounded-lg transition-all flex items-center space-x-1.5 ${
              activeTab === 'stats'
                ? 'bg-white text-indigo-700 shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <BarChart3 className="w-3.5 h-3.5" />
            <span>Course Statistics</span>
          </button>
        </div>
      </div>

      {/* Course & Date Filter Header */}
      <div className="bg-white p-5 rounded-xl shadow-xs border border-slate-200 grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
        <div>
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
            Select Course
          </label>
          <select
            value={selectedCourseId}
            onChange={(e) => setSelectedCourseId(e.target.value)}
            className="w-full px-3.5 py-2 text-sm bg-slate-50 border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
          >
            {courses.map((c) => (
              <option key={c.id} value={c.id}>
                {c.code} - {c.name} ({c.departmentName})
              </option>
            ))}
          </select>
        </div>

        {activeTab === 'mark' && (
          <div>
            <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
              Attendance Date
            </label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="w-full px-3.5 py-2 text-sm bg-slate-50 border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
            />
          </div>
        )}

        {activeTab === 'mark' && (
          <div className="flex flex-wrap items-center gap-2 pt-2 md:pt-5 justify-end">
            <button
              type="button"
              onClick={() => setAllStatus('PRESENT')}
              className="px-3 py-1.5 text-xs font-semibold text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 rounded-lg transition-colors flex items-center space-x-1"
            >
              <CheckCheck className="w-3.5 h-3.5" />
              <span>All Present</span>
            </button>
            <button
              type="button"
              onClick={() => setAllStatus('ABSENT')}
              className="px-3 py-1.5 text-xs font-semibold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 rounded-lg transition-colors flex items-center space-x-1"
            >
              <XCircle className="w-3.5 h-3.5" />
              <span>All Absent</span>
            </button>
          </div>
        )}
      </div>

      {/* Main Content Area */}
      {activeTab === 'mark' ? (
        <div className="space-y-4">
          {loading ? (
            <div className="bg-white rounded-xl p-16 flex justify-center border border-slate-200">
              <Loader size="lg" />
            </div>
          ) : students.length === 0 ? (
            <EmptyState
              icon={Users}
              title="No Enrolled Students"
              description={`There are currently no active students enrolled in ${selectedCourse?.name || 'this course'}. Enroll students first to record attendance.`}
            />
          ) : (
            <div className="bg-white rounded-xl shadow-xs border border-slate-200 overflow-hidden">
              <div className="p-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
                <span className="text-sm font-semibold text-slate-700">
                  Enrolled Students ({students.length})
                </span>
                <span className="text-xs text-slate-500 font-medium">
                  Showing class register for {date}
                </span>
              </div>

              <div className="divide-y divide-slate-100">
                {students.map((st) => {
                  const currentStatus = attendanceMap[st.studentId] || 'PRESENT';
                  return (
                    <div
                      key={st.studentId}
                      className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50/70 transition-colors"
                    >
                      <div className="flex items-center space-x-3">
                        <span className="font-mono text-xs font-bold text-indigo-700 bg-indigo-50 px-2.5 py-1 rounded">
                          {st.studentRollNumber}
                        </span>
                        <div>
                          <p className="text-sm font-semibold text-slate-800">{st.studentName}</p>
                          <p className="text-xs text-slate-500">{st.studentEmail}</p>
                        </div>
                      </div>

                      {/* Status Selector */}
                      <div className="flex items-center space-x-2">
                        <button
                          type="button"
                          onClick={() => handleStatusChange(st.studentId, 'PRESENT')}
                          className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition-all ${
                            currentStatus === 'PRESENT'
                              ? 'bg-emerald-600 text-white shadow-xs'
                              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                          }`}
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          <span>Present</span>
                        </button>

                        <button
                          type="button"
                          onClick={() => handleStatusChange(st.studentId, 'LATE')}
                          className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition-all ${
                            currentStatus === 'LATE'
                              ? 'bg-amber-500 text-white shadow-xs'
                              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                          }`}
                        >
                          <Clock className="w-3.5 h-3.5" />
                          <span>Late</span>
                        </button>

                        <button
                          type="button"
                          onClick={() => handleStatusChange(st.studentId, 'ABSENT')}
                          className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition-all ${
                            currentStatus === 'ABSENT'
                              ? 'bg-rose-600 text-white shadow-xs'
                              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                          }`}
                        >
                          <XCircle className="w-3.5 h-3.5" />
                          <span>Absent</span>
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Bottom Sticky Action Bar */}
              <div className="p-4 bg-slate-50 border-t border-slate-200 flex justify-end">
                <button
                  type="button"
                  onClick={handleSaveAttendance}
                  disabled={saving}
                  className="inline-flex items-center space-x-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition-all disabled:opacity-50"
                >
                  <Save className="w-4 h-4" />
                  <span>{saving ? 'Saving...' : 'Save Attendance'}</span>
                </button>
              </div>
            </div>
          )}
        </div>
      ) : (
        /* Stats Tab */
        <div>
          {statsLoading ? (
            <div className="bg-white rounded-xl p-16 flex justify-center border border-slate-200">
              <Loader size="lg" />
            </div>
          ) : stats.length === 0 ? (
            <EmptyState
              icon={BarChart3}
              title="No Attendance Data"
              description="No attendance records have been registered for this course yet."
            />
          ) : (
            <div className="bg-white rounded-xl shadow-xs border border-slate-200 overflow-hidden">
              <div className="p-4 bg-slate-50 border-b border-slate-200">
                <h3 className="text-sm font-bold text-slate-700">Course Attendance Overview</h3>
              </div>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-slate-200">
                  <thead className="bg-slate-50 text-slate-600 text-xs font-semibold uppercase tracking-wider">
                    <tr>
                      <th className="px-6 py-3 text-left">Roll Number</th>
                      <th className="px-6 py-3 text-left">Student</th>
                      <th className="px-6 py-3 text-center">Present</th>
                      <th className="px-6 py-3 text-center">Late</th>
                      <th className="px-6 py-3 text-center">Absent</th>
                      <th className="px-6 py-3 text-center">Total Sessions</th>
                      <th className="px-6 py-3 text-left">Attendance %</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 text-sm">
                    {stats.map((row) => {
                      const isLow = row.attendancePercentage < 75;
                      return (
                        <tr key={row.studentId} className="hover:bg-slate-50/60 transition-colors">
                          <td className="px-6 py-4 whitespace-nowrap font-mono text-xs font-bold text-indigo-700">
                            {row.studentRollNumber}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap font-medium text-slate-800">
                            {row.studentName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center text-emerald-700 font-semibold">
                            {row.presentCount}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center text-amber-700 font-semibold">
                            {row.lateCount}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center text-rose-700 font-semibold">
                            {row.absentCount}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center text-slate-600 font-semibold">
                            {row.totalClasses}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div className="w-48">
                              <div className="flex justify-between items-center text-xs mb-1">
                                <span className={`font-bold ${isLow ? 'text-rose-600' : 'text-emerald-600'}`}>
                                  {row.attendancePercentage.toFixed(1)}%
                                </span>
                                {isLow && (
                                  <span className="text-[10px] bg-rose-100 text-rose-700 px-1.5 py-0.2 rounded font-semibold">
                                    Low Attendance
                                  </span>
                                )}
                              </div>
                              <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                                <div
                                  className={`h-2 rounded-full transition-all ${
                                    isLow ? 'bg-rose-500' : 'bg-emerald-500'
                                  }`}
                                  style={{ width: `${Math.min(row.attendancePercentage, 100)}%` }}
                                />
                              </div>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
