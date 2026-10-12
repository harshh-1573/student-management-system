import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { courseApi } from '../../api/courseApi';
import { enrollmentApi } from '../../api/enrollmentApi';
import { markApi } from '../../api/markApi';
import Loader from '../../components/common/Loader';
import EmptyState from '../../components/common/EmptyState';
import { Award, Save, BookOpen, AlertCircle, CheckCircle, Calculator } from 'lucide-react';
import toast from 'react-hot-toast';

export default function MarksPage() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [examType, setExamType] = useState('MIDTERM');
  const [maxMarks, setMaxMarks] = useState(100);

  const [students, setStudents] = useState([]); // active enrolled students
  const [marksMap, setMarksMap] = useState({}); // studentId -> marksObtained
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    if (selectedCourseId) {
      fetchCourseMarksAndRoster(selectedCourseId, examType);
    }
  }, [selectedCourseId, examType]);

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

  const fetchCourseMarksAndRoster = async (courseId, selectedExamType) => {
    setLoading(true);
    try {
      // 1. Fetch active enrollments
      const enrollRes = await enrollmentApi.getByCourse(courseId, { page: 0, size: 200 });
      const enrollments = enrollRes.data?.content || [];
      const activeEnrollments = enrollments.filter((e) => e.status === 'ACTIVE');
      setStudents(activeEnrollments);

      // 2. Fetch already recorded marks for this course and examType
      const marksRes = await markApi.getByCourse(courseId, selectedExamType);
      const recordedMarks = marksRes.data || [];

      // Determine max marks if previously recorded
      if (recordedMarks.length > 0 && recordedMarks[0].maxMarks) {
        setMaxMarks(recordedMarks[0].maxMarks);
      } else {
        setMaxMarks(selectedExamType === 'ASSIGNMENT' ? 25 : 100);
      }

      // Populate initial marks map
      const initialMap = {};
      activeEnrollments.forEach((e) => {
        const found = recordedMarks.find((m) => m.studentId === e.studentId);
        initialMap[e.studentId] = found ? found.marksObtained : '';
      });
      setMarksMap(initialMap);
    } catch (err) {
      toast.error('Failed to load student marks');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkChange = (studentId, value) => {
    if (value === '') {
      setMarksMap((prev) => ({ ...prev, [studentId]: '' }));
      return;
    }
    const num = parseFloat(value);
    if (isNaN(num)) return;
    if (num < 0) return;
    if (num > maxMarks) {
      toast.error(`Marks cannot exceed maximum marks (${maxMarks})`);
      return;
    }
    setMarksMap((prev) => ({ ...prev, [studentId]: num }));
  };

  const calculateGrade = (obtained, max) => {
    if (obtained === '' || obtained === null || obtained === undefined) return { grade: '-', color: 'text-slate-400' };
    const pct = (Number(obtained) / Number(max)) * 100;
    if (pct >= 90) return { grade: 'A+', color: 'text-emerald-700 bg-emerald-50 border-emerald-200' };
    if (pct >= 80) return { grade: 'A', color: 'text-teal-700 bg-teal-50 border-teal-200' };
    if (pct >= 70) return { grade: 'B', color: 'text-blue-700 bg-blue-50 border-blue-200' };
    if (pct >= 60) return { grade: 'C', color: 'text-amber-700 bg-amber-50 border-amber-200' };
    if (pct >= 50) return { grade: 'D', color: 'text-orange-700 bg-orange-50 border-orange-200' };
    return { grade: 'F', color: 'text-rose-700 bg-rose-50 border-rose-200' };
  };

  const handleSaveMarks = async () => {
    if (!selectedCourseId) return;
    if (students.length === 0) {
      toast.error('No enrolled students in this course');
      return;
    }
    if (!maxMarks || Number(maxMarks) <= 0) {
      toast.error('Please enter a valid Maximum Marks value');
      return;
    }

    // Filter items with valid marks entered
    const items = [];
    for (const st of students) {
      const val = marksMap[st.studentId];
      if (val !== '' && val !== null && val !== undefined) {
        items.push({
          studentId: st.studentId,
          marksObtained: Number(val),
        });
      }
    }

    if (items.length === 0) {
      toast.error('Please enter marks for at least one student');
      return;
    }

    setSaving(true);
    try {
      await markApi.saveBatch({
        courseId: Number(selectedCourseId),
        examType: examType,
        maxMarks: Number(maxMarks),
        items: items,
      });

      toast.success(`Marks for ${items.length} students recorded successfully`);
      fetchCourseMarksAndRoster(selectedCourseId, examType);
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to record marks';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const selectedCourse = courses.find((c) => c.id.toString() === selectedCourseId);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 tracking-tight">Examination Marks & Grades</h1>
          <p className="text-sm text-slate-500 mt-1">
            Enter exam scores, compute percentage, and assign 10-point GPA letter grades
          </p>
        </div>
      </div>

      {/* Control Bar */}
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

        <div>
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
            Exam Assessment Type
          </label>
          <select
            value={examType}
            onChange={(e) => setExamType(e.target.value)}
            className="w-full px-3.5 py-2 text-sm bg-slate-50 border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
          >
            <option value="MIDTERM">Midterm Exam</option>
            <option value="FINAL">Final Examination</option>
            <option value="ASSIGNMENT">Continuous Assignment</option>
          </select>
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
            Maximum Marks
          </label>
          <input
            type="number"
            min="1"
            value={maxMarks}
            onChange={(e) => setMaxMarks(e.target.value)}
            className="w-full px-3.5 py-2 text-sm bg-slate-50 border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
          />
        </div>
      </div>

      {/* Content Table */}
      {loading ? (
        <div className="bg-white rounded-xl p-16 flex justify-center border border-slate-200">
          <Loader size="lg" />
        </div>
      ) : students.length === 0 ? (
        <EmptyState
          icon={Award}
          title="No Students Enrolled"
          description={`There are currently no active students enrolled in ${selectedCourse?.name || 'this course'}. Enroll students first to grade them.`}
        />
      ) : (
        <div className="bg-white rounded-xl shadow-xs border border-slate-200 overflow-hidden">
          <div className="p-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
            <span className="text-sm font-semibold text-slate-700">
              Grade Sheet: {selectedCourse?.name} ({examType})
            </span>
            <span className="text-xs text-slate-500 font-medium">
              Max Score: <span className="font-bold text-indigo-700">{maxMarks} pts</span>
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200">
              <thead className="bg-slate-50 text-slate-600 text-xs font-semibold uppercase tracking-wider">
                <tr>
                  <th className="px-6 py-3 text-left">Roll Number</th>
                  <th className="px-6 py-3 text-left">Student Name</th>
                  <th className="px-6 py-3 text-center">Max Marks</th>
                  <th className="px-6 py-3 text-left">Marks Obtained</th>
                  <th className="px-6 py-3 text-center">Score %</th>
                  <th className="px-6 py-3 text-center">Grade Preview</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-sm">
                {students.map((st) => {
                  const val = marksMap[st.studentId];
                  const hasVal = val !== '' && val !== null && val !== undefined;
                  const pct = hasVal ? ((Number(val) / Number(maxMarks)) * 100).toFixed(1) : '-';
                  const gradeInfo = calculateGrade(val, maxMarks);

                  return (
                    <tr key={st.studentId} className="hover:bg-slate-50/70 transition-colors">
                      <td className="px-6 py-4 whitespace-nowrap font-mono text-xs font-bold text-indigo-700">
                        {st.studentRollNumber}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap font-medium text-slate-800">
                        {st.studentName}
                        <div className="text-xs text-slate-400 font-normal">{st.studentEmail}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-center text-slate-500 font-mono text-xs">
                        {maxMarks}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="w-32">
                          <input
                            type="number"
                            min="0"
                            max={maxMarks}
                            step="0.5"
                            value={val ?? ''}
                            onChange={(e) => handleMarkChange(st.studentId, e.target.value)}
                            placeholder="0.0"
                            className="w-full px-3 py-1.5 text-sm bg-slate-50 border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
                          />
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-center font-bold text-slate-700">
                        {hasVal ? `${pct}%` : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-center">
                        <span
                          className={`inline-block px-2.5 py-0.5 rounded-md text-xs font-bold border ${gradeInfo.color}`}
                        >
                          {gradeInfo.grade}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {/* Action Bar */}
          <div className="p-4 bg-slate-50 border-t border-slate-200 flex justify-end">
            <button
              type="button"
              onClick={handleSaveMarks}
              disabled={saving}
              className="inline-flex items-center space-x-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-medium text-sm rounded-lg shadow-sm transition-all disabled:opacity-50"
            >
              <Save className="w-4 h-4" />
              <span>{saving ? 'Saving...' : 'Save Marks & Calculate Grades'}</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
