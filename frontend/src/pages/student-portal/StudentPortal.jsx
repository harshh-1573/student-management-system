import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { studentApi } from '../../api/studentApi';
import { enrollmentApi } from '../../api/enrollmentApi';
import { attendanceApi } from '../../api/attendanceApi';
import Loader from '../../components/common/Loader';
import EmptyState from '../../components/common/EmptyState';
import {
  User,
  BookOpen,
  CalendarCheck,
  Award,
  GraduationCap,
  Mail,
  Phone,
  MapPin,
  Calendar,
  Building,
  CheckCircle,
  Clock,
  XCircle,
  FileText,
} from 'lucide-react';
import toast from 'react-hot-toast';

export default function StudentPortal() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('report'); // 'report' | 'courses' | 'attendance' | 'profile'
  const [loading, setLoading] = useState(true);

  const [studentProfile, setStudentProfile] = useState(null);
  const [report, setReport] = useState(null);
  const [enrollments, setEnrollments] = useState([]);
  const [attendance, setAttendance] = useState([]);

  useEffect(() => {
    fetchStudentData();
  }, []);

  const fetchStudentData = async () => {
    setLoading(true);
    try {
      const [profileRes, reportRes, enrollRes, attRes] = await Promise.allSettled([
        studentApi.getMe(),
        studentApi.getMyReport(),
        enrollmentApi.getMyEnrollments(),
        attendanceApi.getMyAttendance(),
      ]);

      if (profileRes.status === 'fulfilled') setStudentProfile(profileRes.value.data);
      if (reportRes.status === 'fulfilled') setReport(reportRes.value.data);
      if (enrollRes.status === 'fulfilled') setEnrollments(enrollRes.value.data || []);
      if (attRes.status === 'fulfilled') setAttendance(attRes.value.data || []);
    } catch (err) {
      toast.error('Failed to load student portal details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <Loader size="lg" />
      </div>
    );
  }

  const profile = studentProfile || report?.student;

  return (
    <div className="space-y-6">
      {/* Top Banner Card */}
      <div className="relative overflow-hidden bg-gradient-to-r from-indigo-700 via-indigo-800 to-purple-800 rounded-2xl p-6 md:p-8 text-white shadow-lg">
        <div className="relative z-10 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl bg-white/10 backdrop-blur-md border border-white/20 flex items-center justify-center text-white font-bold text-2xl shadow-inner">
              {profile?.name?.charAt(0) || 'S'}
            </div>
            <div>
              <div className="flex items-center space-x-3">
                <h1 className="text-2xl font-bold">{profile?.name || user?.name}</h1>
                <span className="font-mono text-xs bg-indigo-500/40 border border-white/30 px-2.5 py-0.5 rounded-full font-semibold">
                  {profile?.rollNumber || 'STUDENT'}
                </span>
              </div>
              <p className="text-indigo-200 text-sm mt-1">
                Department of {profile?.departmentName || 'Engineering'} • Enrollment Year: {profile?.enrollmentYear || '2024'}
              </p>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center space-x-6 bg-white/10 backdrop-blur-md border border-white/15 px-6 py-3 rounded-xl">
            <div className="text-center">
              <span className="text-xs uppercase font-medium text-indigo-200 tracking-wider">
                Cumulative GPA
              </span>
              <p className="text-2xl font-black text-amber-300">
                {report?.cumulativeGpa !== undefined ? report.cumulativeGpa.toFixed(2) : '0.00'}
                <span className="text-xs font-normal text-indigo-200"> / 10.0</span>
              </p>
            </div>
            <div className="w-px h-8 bg-white/20" />
            <div className="text-center">
              <span className="text-xs uppercase font-medium text-indigo-200 tracking-wider">
                Total Credits
              </span>
              <p className="text-2xl font-black text-white">{report?.totalCredits || 0}</p>
            </div>
          </div>
        </div>

        {/* Background decorative circles */}
        <div className="absolute -right-12 -top-12 w-48 h-48 bg-white/5 rounded-full blur-2xl pointer-events-none" />
        <div className="absolute right-36 -bottom-16 w-56 h-56 bg-purple-500/10 rounded-full blur-2xl pointer-events-none" />
      </div>

      {/* Tabs Navigation */}
      <div className="flex border-b border-slate-200 space-x-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('report')}
          className={`pb-3 px-4 text-sm font-semibold flex items-center space-x-2 border-b-2 transition-all whitespace-nowrap ${
            activeTab === 'report'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <Award className="w-4 h-4" />
          <span>Academic Report & GPA</span>
        </button>

        <button
          onClick={() => setActiveTab('courses')}
          className={`pb-3 px-4 text-sm font-semibold flex items-center space-x-2 border-b-2 transition-all whitespace-nowrap ${
            activeTab === 'courses'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <BookOpen className="w-4 h-4" />
          <span>My Enrolled Courses ({enrollments.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('attendance')}
          className={`pb-3 px-4 text-sm font-semibold flex items-center space-x-2 border-b-2 transition-all whitespace-nowrap ${
            activeTab === 'attendance'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <CalendarCheck className="w-4 h-4" />
          <span>Attendance Record</span>
        </button>

        <button
          onClick={() => setActiveTab('profile')}
          className={`pb-3 px-4 text-sm font-semibold flex items-center space-x-2 border-b-2 transition-all whitespace-nowrap ${
            activeTab === 'profile'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <User className="w-4 h-4" />
          <span>Profile Details</span>
        </button>
      </div>

      {/* TAB 1: Academic Report & GPA */}
      {activeTab === 'report' && (
        <div className="space-y-6">
          {!report || report.courses.length === 0 ? (
            <EmptyState
              icon={GraduationCap}
              title="No Academic Records"
              description="You are not currently enrolled in any courses or no examination grades have been submitted yet."
            />
          ) : (
            <div className="bg-white rounded-xl shadow-xs border border-slate-200 overflow-hidden">
              <div className="p-5 bg-slate-50 border-b border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div>
                  <h3 className="font-bold text-slate-800">Transcript of Academic Performance</h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Official grade card with course credits, attendance, examination scores, and SGPA
                  </p>
                </div>
                <div className="flex items-center space-x-2 bg-indigo-50 border border-indigo-100 px-3 py-1.5 rounded-lg text-indigo-800 font-bold text-sm">
                  <span>CGPA:</span>
                  <span className="text-indigo-600 text-base">{report.cumulativeGpa.toFixed(2)}</span>
                </div>
              </div>

              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-slate-200">
                  <thead className="bg-slate-50 text-slate-600 text-xs font-semibold uppercase tracking-wider">
                    <tr>
                      <th className="px-6 py-3 text-left">Course Code</th>
                      <th className="px-6 py-3 text-left">Course Title</th>
                      <th className="px-6 py-3 text-center">Credits</th>
                      <th className="px-6 py-3 text-center">Attendance %</th>
                      <th className="px-6 py-3 text-center">Marks Obtained</th>
                      <th className="px-6 py-3 text-center">Overall %</th>
                      <th className="px-6 py-3 text-center">Grade</th>
                      <th className="px-6 py-3 text-center">Grade Point</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 text-sm">
                    {report.courses.map((c) => {
                      const attColor =
                        c.attendancePercentage >= 75 ? 'text-emerald-600' : 'text-rose-600 font-bold';
                      return (
                        <tr key={c.courseId} className="hover:bg-slate-50/60 transition-colors">
                          <td className="px-6 py-4 whitespace-nowrap font-mono text-xs font-bold text-indigo-700">
                            {c.courseCode}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap font-medium text-slate-800">
                            {c.courseName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center text-slate-600 font-semibold">
                            {c.credits}
                          </td>
                          <td className={`px-6 py-4 whitespace-nowrap text-center ${attColor}`}>
                            {c.attendancePercentage.toFixed(1)}%
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center font-mono text-xs text-slate-700">
                            {c.totalMarksObtained} / {c.totalMaxMarks}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center font-bold text-slate-800">
                            {c.overallPercentage.toFixed(1)}%
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center">
                            <span
                              className={`inline-block px-2.5 py-0.5 rounded-md text-xs font-bold ${
                                c.grade === 'A+' || c.grade === 'A'
                                  ? 'bg-emerald-100 text-emerald-800'
                                  : c.grade === 'B'
                                  ? 'bg-blue-100 text-blue-800'
                                  : c.grade === 'C'
                                  ? 'bg-amber-100 text-amber-800'
                                  : 'bg-rose-100 text-rose-800'
                              }`}
                            >
                              {c.grade}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-center font-black text-indigo-700">
                            {c.gradePoint.toFixed(1)}
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

      {/* TAB 2: My Enrolled Courses */}
      {activeTab === 'courses' && (
        <div className="space-y-4">
          {enrollments.length === 0 ? (
            <EmptyState
              icon={BookOpen}
              title="No Courses Enrolled"
              description="You have not been enrolled in any courses for the current semester."
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
              {enrollments.map((en) => (
                <div
                  key={en.id}
                  className="bg-white rounded-xl shadow-xs border border-slate-200 p-5 hover:shadow-md transition-shadow flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-center justify-between mb-3">
                      <span className="font-mono text-xs font-bold bg-indigo-50 text-indigo-700 px-2 py-0.5 rounded">
                        {en.courseCode}
                      </span>
                      <span
                        className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                          en.status === 'ACTIVE'
                            ? 'bg-emerald-100 text-emerald-800'
                            : 'bg-rose-100 text-rose-800'
                        }`}
                      >
                        {en.status}
                      </span>
                    </div>
                    <h4 className="text-base font-bold text-slate-800">{en.courseName}</h4>
                    <p className="text-xs text-slate-500 mt-1">Semester: {en.semester || 'Current'}</p>
                  </div>

                  <div className="mt-5 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
                    <span className="font-medium">Credits: {en.credits}</span>
                    <span>Enrolled: {en.enrollmentDate}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: Attendance Record */}
      {activeTab === 'attendance' && (
        <div className="space-y-6">
          {/* Course-wise Attendance Progress Bars */}
          {report?.courses && report.courses.length > 0 && (
            <div className="bg-white rounded-xl shadow-xs border border-slate-200 p-6">
              <h3 className="font-bold text-slate-800 mb-4 text-sm">Course Attendance Summary</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {report.courses.map((c) => {
                  const pct = c.attendancePercentage;
                  const isSafe = pct >= 75;
                  return (
                    <div key={c.courseId} className="space-y-1.5">
                      <div className="flex justify-between items-center text-xs">
                        <span className="font-semibold text-slate-700">
                          {c.courseCode} - {c.courseName}
                        </span>
                        <span className={`font-bold ${isSafe ? 'text-emerald-600' : 'text-rose-600'}`}>
                          {pct.toFixed(1)}% {isSafe ? '✓' : '⚠️ Low'}
                        </span>
                      </div>
                      <div className="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                        <div
                          className={`h-2.5 rounded-full transition-all ${
                            isSafe ? 'bg-emerald-500' : 'bg-rose-500'
                          }`}
                          style={{ width: `${Math.min(pct, 100)}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Historical Attendance Table */}
          {attendance.length === 0 ? (
            <EmptyState
              icon={CalendarCheck}
              title="No Attendance Logs"
              description="No daily attendance sessions have been registered for you yet."
            />
          ) : (
            <div className="bg-white rounded-xl shadow-xs border border-slate-200 overflow-hidden">
              <div className="p-4 bg-slate-50 border-b border-slate-200">
                <h3 className="font-bold text-slate-700 text-sm">Attendance History Log</h3>
              </div>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-slate-200">
                  <thead className="bg-slate-50 text-slate-600 text-xs font-semibold uppercase tracking-wider">
                    <tr>
                      <th className="px-6 py-3 text-left">Date</th>
                      <th className="px-6 py-3 text-left">Course</th>
                      <th className="px-6 py-3 text-center">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 text-sm">
                    {attendance.map((att) => (
                      <tr key={att.id} className="hover:bg-slate-50/60 transition-colors">
                        <td className="px-6 py-3.5 whitespace-nowrap text-slate-700 font-medium">
                          {att.date}
                        </td>
                        <td className="px-6 py-3.5 whitespace-nowrap font-medium text-slate-800">
                          {att.courseName}{' '}
                          <span className="text-xs text-slate-500 font-mono">({att.courseCode})</span>
                        </td>
                        <td className="px-6 py-3.5 whitespace-nowrap text-center">
                          {att.status === 'PRESENT' && (
                            <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
                              <CheckCircle className="w-3 h-3" />
                              <span>Present</span>
                            </span>
                          )}
                          {att.status === 'LATE' && (
                            <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-800">
                              <Clock className="w-3 h-3" />
                              <span>Late</span>
                            </span>
                          )}
                          {att.status === 'ABSENT' && (
                            <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-100 text-rose-800">
                              <XCircle className="w-3 h-3" />
                              <span>Absent</span>
                            </span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 4: Profile Details */}
      {activeTab === 'profile' && (
        <div className="bg-white rounded-xl shadow-xs border border-slate-200 p-6 max-w-3xl">
          <h3 className="font-bold text-slate-800 text-base mb-6 border-b border-slate-100 pb-3">
            Personal & Academic Information
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Full Name
              </label>
              <div className="flex items-center space-x-2 text-slate-800 font-semibold text-sm">
                <User className="w-4 h-4 text-indigo-600" />
                <span>{profile?.name}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Roll Number
              </label>
              <div className="font-mono text-sm font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded inline-block">
                {profile?.rollNumber}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Email Address
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <Mail className="w-4 h-4 text-slate-400" />
                <span>{profile?.email}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Phone Number
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <Phone className="w-4 h-4 text-slate-400" />
                <span>{profile?.phone || 'Not provided'}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Department
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <Building className="w-4 h-4 text-slate-400" />
                <span>{profile?.departmentName}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Enrollment Year
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <Calendar className="w-4 h-4 text-slate-400" />
                <span>{profile?.enrollmentYear}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Date of Birth
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <Calendar className="w-4 h-4 text-slate-400" />
                <span>{profile?.dateOfBirth || 'Not provided'}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">
                Residential Address
              </label>
              <div className="flex items-center space-x-2 text-slate-700 text-sm">
                <MapPin className="w-4 h-4 text-slate-400" />
                <span>{profile?.address || 'Not provided'}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
