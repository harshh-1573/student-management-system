import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { studentApi } from '../../api/studentApi';
import { Loader } from '../../components/common/Loader';
import {
  ArrowLeft,
  GraduationCap,
  Award,
  BookOpen,
  CalendarCheck,
  User,
  Mail,
  Phone,
  MapPin,
  Calendar,
} from 'lucide-react';
import toast from 'react-hot-toast';

export const StudentDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReport();
  }, [id]);

  const fetchReport = async () => {
    try {
      setLoading(true);
      const res = await studentApi.getReport(id);
      if (res.success && res.data) {
        setReport(res.data);
      }
    } catch (err) {
      toast.error('Failed to load student academic report');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loader size="lg" text="Loading comprehensive academic report..." />;
  }

  if (!report || !report.student) {
    return (
      <div className="text-center py-12">
        <p className="text-slate-500 mb-4">Student report not found.</p>
        <button
          onClick={() => navigate('/students')}
          className="px-4 py-2 bg-indigo-600 text-white rounded-xl text-xs font-semibold"
        >
          Back to Students
        </button>
      </div>
    );
  }

  const { student, courses, cumulativeGpa, totalCredits, totalEnrolledCourses } = report;

  const getGpaBadge = (gpa) => {
    if (gpa >= 9.0) return 'bg-emerald-50 text-emerald-700 border-emerald-200';
    if (gpa >= 7.5) return 'bg-indigo-50 text-indigo-700 border-indigo-200';
    if (gpa >= 6.0) return 'bg-amber-50 text-amber-700 border-amber-200';
    return 'bg-rose-50 text-rose-700 border-rose-200';
  };

  const getAttendanceBarColor = (pct) => {
    if (pct >= 85) return 'bg-emerald-500';
    if (pct >= 75) return 'bg-indigo-500';
    if (pct >= 60) return 'bg-amber-500';
    return 'bg-rose-500';
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Top back button */}
      <div>
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center space-x-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Students List</span>
        </button>
      </div>

      {/* Student Profile Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-100 shadow-sm">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6 pb-6 border-b border-slate-100">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl bg-indigo-600 text-white flex items-center justify-center font-bold text-xl shadow-lg shadow-indigo-200">
              {student.name.charAt(0)}
            </div>
            <div>
              <div className="flex items-center space-x-3">
                <h1 className="text-2xl font-bold text-slate-900">{student.name}</h1>
                <span className="font-mono text-xs font-bold px-2.5 py-1 bg-slate-100 text-slate-700 rounded-md">
                  {student.rollNumber}
                </span>
              </div>
              <p className="text-xs text-slate-500 mt-1">
                Department of {student.departmentName} &bull; Class of {student.enrollmentYear}
              </p>
            </div>
          </div>

          {/* Cumulative GPA Pill */}
          <div className="flex items-center space-x-3 bg-slate-50 p-4 rounded-2xl border border-slate-100">
            <div className="text-right">
              <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                Cumulative GPA
              </p>
              <p className="text-2xl font-black text-indigo-600">{cumulativeGpa.toFixed(2)}</p>
            </div>
            <div
              className={`w-12 h-12 rounded-xl flex items-center justify-center border font-bold text-sm ${getGpaBadge(
                cumulativeGpa
              )}`}
            >
              <Award className="w-6 h-6" />
            </div>
          </div>
        </div>

        {/* Student Contact Info Row */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 pt-6 text-xs text-slate-600">
          <div className="flex items-center space-x-2">
            <Mail className="w-4 h-4 text-slate-400" />
            <span>{student.email}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Phone className="w-4 h-4 text-slate-400" />
            <span>{student.phone || 'No phone recorded'}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Calendar className="w-4 h-4 text-slate-400" />
            <span>DOB: {student.dateOfBirth || '-'}</span>
          </div>
          <div className="flex items-center space-x-2">
            <MapPin className="w-4 h-4 text-slate-400" />
            <span className="truncate">{student.address || 'Campus Residence'}</span>
          </div>
        </div>
      </div>

      {/* Academic Metrics Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <BookOpen className="w-6 h-6" />
          </div>
          <div>
            <p className="text-[11px] font-semibold text-slate-400 uppercase">Enrolled Courses</p>
            <p className="text-xl font-bold text-slate-800">{totalEnrolledCourses}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
            <GraduationCap className="w-6 h-6" />
          </div>
          <div>
            <p className="text-[11px] font-semibold text-slate-400 uppercase">Credits Earned</p>
            <p className="text-xl font-bold text-slate-800">{totalCredits}</p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <p className="text-[11px] font-semibold text-slate-400 uppercase">10-Point Scale GPA</p>
            <p className="text-xl font-bold text-slate-800">{cumulativeGpa.toFixed(2)} / 10.0</p>
          </div>
        </div>
      </div>

      {/* Course Performance Breakdown */}
      <div className="space-y-4">
        <h3 className="text-lg font-bold text-slate-900 tracking-tight">Course Performance & Grades</h3>

        {courses && courses.length > 0 ? (
          <div className="space-y-4">
            {courses.map((course) => (
              <div
                key={course.courseId}
                className="bg-white rounded-2xl p-6 border border-slate-100 shadow-sm hover:border-slate-200 transition-all"
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-100">
                  <div>
                    <div className="flex items-center space-x-2">
                      <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-indigo-50 text-indigo-700">
                        {course.courseCode}
                      </span>
                      <h4 className="font-bold text-slate-800 text-base">{course.courseName}</h4>
                    </div>
                    <p className="text-xs text-slate-400 mt-1">
                      {course.semester} &bull; {course.credits} Credits
                    </p>
                  </div>

                  <div className="flex items-center space-x-6">
                    {/* Attendance percentage badge */}
                    <div className="w-36">
                      <div className="flex justify-between text-xs font-semibold mb-1">
                        <span className="text-slate-500">Attendance</span>
                        <span className="text-slate-700">{course.attendancePercentage}%</span>
                      </div>
                      <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full transition-all duration-500 ${getAttendanceBarColor(
                            course.attendancePercentage
                          )}`}
                          style={{ width: `${Math.min(course.attendancePercentage, 100)}%` }}
                        />
                      </div>
                    </div>

                    {/* Grade Badge */}
                    <div className="text-center px-4 py-1.5 rounded-xl bg-slate-50 border border-slate-100">
                      <p className="text-[10px] uppercase font-bold text-slate-400">Grade</p>
                      <p className="text-lg font-black text-indigo-600">{course.grade || 'N/A'}</p>
                    </div>

                    {/* Grade Point */}
                    <div className="text-center px-4 py-1.5 rounded-xl bg-slate-50 border border-slate-100">
                      <p className="text-[10px] uppercase font-bold text-slate-400">Point</p>
                      <p className="text-lg font-black text-slate-800">{course.gradePoint}</p>
                    </div>
                  </div>
                </div>

                {/* Exam Marks Table */}
                <div className="pt-4">
                  <p className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">
                    Examination Breakdown
                  </p>
                  {course.marks && course.marks.length > 0 ? (
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                      {course.marks.map((m) => (
                        <div
                          key={m.id}
                          className="bg-slate-50/70 p-3 rounded-xl border border-slate-100 flex items-center justify-between"
                        >
                          <div>
                            <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">
                              {m.examType}
                            </span>
                            <p className="text-sm font-bold text-slate-800 mt-0.5">
                              {m.marksObtained} / {m.maxMarks}
                            </p>
                          </div>
                          <span className="text-xs font-bold text-indigo-600 bg-white px-2 py-0.5 rounded-md border border-slate-200">
                            {m.percentage}%
                          </span>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-xs text-slate-400 italic">No exam marks entered for this course yet.</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-2xl p-12 text-center border border-slate-100">
            <p className="text-slate-500 text-sm">No active course enrollments found for this student.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDetailPage;
