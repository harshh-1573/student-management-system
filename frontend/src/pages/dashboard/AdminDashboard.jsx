import React, { useState, useEffect } from 'react';
import { dashboardApi } from '../../api/dashboardApi';
import { StatCard } from '../../components/common/StatCard';
import { Loader } from '../../components/common/Loader';
import { Users, GraduationCap, BookOpen, Building2, UserCheck, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';

export const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const res = await dashboardApi.getStats();
      if (res.success) {
        setStats(res.data);
      }
    } catch (err) {
      console.error('Failed to load dashboard stats:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loader size="lg" text="Loading dashboard metrics..." />;
  }

  // Prepare bar chart data (Students per Department)
  const deptData = stats?.studentsPerDepartment
    ? Object.entries(stats.studentsPerDepartment).map(([name, count]) => ({
        name,
        students: count,
      }))
    : [];

  // Prepare pie chart data (Enrollments per Course)
  const courseData = stats?.enrollmentsPerCourse
    ? Object.entries(stats.enrollmentsPerCourse).map(([name, count]) => ({
        name,
        value: count,
      }))
    : [];

  const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#06b6d4'];

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">System Overview</h1>
        <p className="text-xs text-slate-500 mt-1">
          Real-time metrics, academic distributions, and operational summary
        </p>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <StatCard
          title="Total Students"
          value={stats?.totalStudents}
          icon={Users}
          color="indigo"
          subtitle="Enrolled learners"
        />
        <StatCard
          title="Faculty Members"
          value={stats?.totalTeachers}
          icon={GraduationCap}
          color="emerald"
          subtitle="Active teachers"
        />
        <StatCard
          title="Course Catalog"
          value={stats?.totalCourses}
          icon={BookOpen}
          color="blue"
          subtitle="Offered subjects"
        />
        <StatCard
          title="Departments"
          value={stats?.totalDepartments}
          icon={Building2}
          color="amber"
          subtitle="Academic wings"
        />
        <StatCard
          title="Active Enrollments"
          value={stats?.totalEnrollments}
          icon={UserCheck}
          color="purple"
          subtitle="Course registrations"
        />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Bar Chart: Students per Department */}
        <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-1">Students by Department</h3>
          <p className="text-xs text-slate-400 mb-6">Distribution across academic faculties</p>
          <div className="h-72 w-full">
            {deptData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={deptData} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis
                    dataKey="name"
                    tick={{ fontSize: 11, fill: '#64748b' }}
                    interval={0}
                    angle={-15}
                    textAnchor="end"
                  />
                  <YAxis tick={{ fontSize: 11, fill: '#64748b' }} allowDecimals={false} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      borderRadius: '12px',
                      border: '1px solid #e2e8f0',
                      boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
                      fontSize: '12px',
                    }}
                  />
                  <Bar dataKey="students" fill="#6366f1" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No department data available
              </div>
            )}
          </div>
        </div>

        {/* Pie Chart: Enrollments by Course */}
        <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-1">Enrollments by Course</h3>
          <p className="text-xs text-slate-400 mb-6">Active students registered per subject</p>
          <div className="h-72 w-full">
            {courseData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={courseData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={85}
                    paddingAngle={4}
                    dataKey="value"
                  >
                    {courseData.map((_, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#fff',
                      borderRadius: '12px',
                      border: '1px solid #e2e8f0',
                      fontSize: '12px',
                    }}
                  />
                  <Legend
                    layout="horizontal"
                    verticalAlign="bottom"
                    align="center"
                    wrapperStyle={{ fontSize: '11px', paddingTop: '10px' }}
                  />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No course enrollment data available
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Quick Access Actions */}
      <div className="bg-gradient-to-r from-indigo-500 to-brand-700 rounded-2xl p-6 text-white shadow-lg shadow-indigo-200">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <h3 className="text-lg font-bold">Academic Management Quick Shortcuts</h3>
            <p className="text-xs text-indigo-100 mt-1">
              Navigate directly to attendance marking, exam grade entry, or enrollment tracking.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <Link
              to="/attendance"
              className="px-4 py-2 bg-white text-indigo-600 rounded-xl text-xs font-semibold hover:bg-indigo-50 transition-all flex items-center space-x-1"
            >
              <span>Attendance</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
            <Link
              to="/marks"
              className="px-4 py-2 bg-indigo-600 border border-indigo-400 text-white rounded-xl text-xs font-semibold hover:bg-indigo-700 transition-all flex items-center space-x-1"
            >
              <span>Marks & Grades</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
            <Link
              to="/students"
              className="px-4 py-2 bg-indigo-600 border border-indigo-400 text-white rounded-xl text-xs font-semibold hover:bg-indigo-700 transition-all flex items-center space-x-1"
            >
              <span>Manage Students</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
