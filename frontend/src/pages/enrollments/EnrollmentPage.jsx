import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { enrollmentApi } from '../../api/enrollmentApi';
import { courseApi } from '../../api/courseApi';
import { studentApi } from '../../api/studentApi';
import DataTable from '../../components/common/DataTable';
import Modal from '../../components/common/Modal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Loader from '../../components/common/Loader';
import EmptyState from '../../components/common/EmptyState';
import Pagination from '../../components/common/Pagination';
import { UserCheck, Plus, Trash2, Filter, AlertCircle, BookOpen } from 'lucide-react';
import toast from 'react-hot-toast';

export default function EnrollmentPage() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [students, setStudents] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Enroll modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalStudentId, setModalStudentId] = useState('');
  const [modalCourseId, setModalCourseId] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Drop dialog state
  const [dropDialogOpen, setDropDialogOpen] = useState(false);
  const [enrollmentToDrop, setEnrollmentToDrop] = useState(null);

  useEffect(() => {
    fetchInitialData();
  }, []);

  useEffect(() => {
    if (selectedCourseId) {
      fetchEnrollments(selectedCourseId, page);
    } else {
      setEnrollments([]);
      setTotalPages(1);
      setTotalElements(0);
    }
  }, [selectedCourseId, page]);

  const fetchInitialData = async () => {
    try {
      const [coursesRes, studentsRes] = await Promise.all([
        courseApi.getAllList(),
        studentApi.getAllList(),
      ]);
      const courseList = coursesRes.data || [];
      setCourses(courseList);
      setStudents(studentsRes.data || []);
      if (courseList.length > 0) {
        setSelectedCourseId(courseList[0].id.toString());
      }
    } catch (err) {
      toast.error('Failed to load courses or students');
    }
  };

  const fetchEnrollments = async (courseId, pageNumber) => {
    setLoading(true);
    try {
      const res = await enrollmentApi.getByCourse(courseId, { page: pageNumber, size: 10 });
      if (res.data) {
        setEnrollments(res.data.content || []);
        setTotalPages(res.data.totalPages || 1);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      toast.error('Failed to load enrollments for selected course');
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async (e) => {
    e.preventDefault();
    if (!modalStudentId || !modalCourseId) {
      toast.error('Please select both a student and a course');
      return;
    }
    setSubmitting(true);
    try {
      await enrollmentApi.enroll({
        studentId: Number(modalStudentId),
        courseId: Number(modalCourseId),
      });
      toast.success('Student enrolled successfully');
      setIsModalOpen(false);
      setModalStudentId('');
      if (selectedCourseId === modalCourseId) {
        fetchEnrollments(selectedCourseId, page);
      } else {
        setSelectedCourseId(modalCourseId);
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to enroll student';
      toast.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDropConfirm = async () => {
    if (!enrollmentToDrop) return;
    try {
      await enrollmentApi.drop(enrollmentToDrop.id);
      toast.success('Enrollment dropped successfully');
      setDropDialogOpen(false);
      fetchEnrollments(selectedCourseId, page);
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to drop enrollment';
      toast.error(msg);
    }
  };

  const columns = [
    {
      header: 'Roll Number',
      accessor: 'studentRollNumber',
      render: (item) => (
        <span className="font-mono text-xs font-semibold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded">
          {item.studentRollNumber}
        </span>
      ),
    },
    {
      header: 'Student Name',
      accessor: 'studentName',
      render: (item) => (
        <div>
          <div className="font-medium text-slate-800">{item.studentName}</div>
          <div className="text-xs text-slate-500">{item.studentEmail}</div>
        </div>
      ),
    },
    {
      header: 'Course',
      render: (item) => (
        <div>
          <span className="font-medium text-slate-800">{item.courseName}</span>
          <span className="ml-2 text-xs text-slate-500 font-mono">({item.courseCode})</span>
        </div>
      ),
    },
    {
      header: 'Enrollment Date',
      accessor: 'enrollmentDate',
      render: (item) => (
        <span className="text-sm text-slate-600">{item.enrollmentDate || 'N/A'}</span>
      ),
    },
    {
      header: 'Status',
      accessor: 'status',
      render: (item) => {
        const isActive = item.status === 'ACTIVE';
        return (
          <span
            className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
              isActive
                ? 'bg-emerald-100 text-emerald-800'
                : 'bg-rose-100 text-rose-800'
            }`}
          >
            {item.status}
          </span>
        );
      },
    },
    {
      header: 'Actions',
      align: 'right',
      render: (item) => (
        <div className="flex justify-end space-x-2">
          {item.status === 'ACTIVE' && (
            <button
              onClick={() => {
                setEnrollmentToDrop(item);
                setDropDialogOpen(true);
              }}
              className="px-2.5 py-1 text-xs font-medium text-rose-600 hover:text-rose-800 bg-rose-50 hover:bg-rose-100 rounded-md transition-colors inline-flex items-center space-x-1"
            >
              <Trash2 className="w-3.5 h-3.5" />
              <span>Drop</span>
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 tracking-tight">Course Enrollments</h1>
          <p className="text-sm text-slate-500 mt-1">
            Manage student course enrollments, class rosters, and course drops
          </p>
        </div>
        <button
          onClick={() => {
            setModalCourseId(selectedCourseId || (courses[0]?.id?.toString() || ''));
            setModalStudentId(students[0]?.id?.toString() || '');
            setIsModalOpen(true);
          }}
          className="inline-flex items-center justify-center space-x-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-lg text-sm font-medium shadow-sm transition-colors"
        >
          <Plus className="w-4 h-4" />
          <span>New Enrollment</span>
        </button>
      </div>

      {/* Course Selector Bar */}
      <div className="bg-white p-4 rounded-xl shadow-xs border border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center space-x-3 w-full sm:w-auto">
          <BookOpen className="w-5 h-5 text-indigo-600 shrink-0" />
          <label className="text-sm font-semibold text-slate-700 whitespace-nowrap">
            Filter by Course:
          </label>
          <select
            value={selectedCourseId}
            onChange={(e) => {
              setSelectedCourseId(e.target.value);
              setPage(0);
            }}
            className="w-full sm:w-80 px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 font-medium text-slate-800"
          >
            {courses.map((c) => (
              <option key={c.id} value={c.id}>
                {c.code} - {c.name} ({c.departmentName})
              </option>
            ))}
          </select>
        </div>
        <div className="text-xs text-slate-500 font-medium">
          Total Enrolled in this Course: <span className="font-bold text-indigo-600">{totalElements}</span>
        </div>
      </div>

      {/* Table Content */}
      {loading ? (
        <div className="bg-white rounded-xl p-12 flex justify-center border border-slate-200">
          <Loader size="lg" />
        </div>
      ) : enrollments.length === 0 ? (
        <EmptyState
          icon={UserCheck}
          title="No Students Enrolled"
          description="There are currently no active or historical enrollments for this course. Click New Enrollment to register a student."
          actionText="Enroll Student Now"
          onAction={() => {
            setModalCourseId(selectedCourseId);
            setIsModalOpen(true);
          }}
        />
      ) : (
        <div className="space-y-4">
          <DataTable columns={columns} data={enrollments} />
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={10}
            onPageChange={(newPage) => setPage(newPage)}
          />
        </div>
      )}

      {/* Enroll Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Enroll Student in Course"
      >
        <form onSubmit={handleEnroll} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Select Student <span className="text-rose-500">*</span>
            </label>
            <select
              value={modalStudentId}
              onChange={(e) => setModalStudentId(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 text-slate-800"
              required
            >
              <option value="">-- Choose Student --</option>
              {students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.rollNumber} - {s.name} ({s.departmentName})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Select Course <span className="text-rose-500">*</span>
            </label>
            <select
              value={modalCourseId}
              onChange={(e) => setModalCourseId(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-hidden focus:ring-2 focus:ring-indigo-500 text-slate-800"
              required
            >
              <option value="">-- Choose Course --</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name} (Credits: {c.credits})
                </option>
              ))}
            </select>
          </div>

          <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-xs text-amber-800 flex items-start space-x-2">
            <AlertCircle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
            <span>
              Enrolling a student binds them to this course's attendance registers, grade books, and academic transcripts. Duplicate enrollments are strictly prohibited.
            </span>
          </div>

          <div className="flex justify-end space-x-3 pt-3 border-t border-slate-200">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-colors disabled:opacity-50"
            >
              {submitting ? 'Enrolling...' : 'Confirm Enrollment'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Drop Dialog */}
      <ConfirmDialog
        isOpen={dropDialogOpen}
        onClose={() => setDropDialogOpen(false)}
        onConfirm={handleDropConfirm}
        title="Drop Student Enrollment"
        message={`Are you sure you want to drop ${enrollmentToDrop?.studentName} (${enrollmentToDrop?.studentRollNumber}) from ${enrollmentToDrop?.courseName}? This will change the enrollment status to DROPPED.`}
        confirmText="Drop Course"
        isDanger={true}
      />
    </div>
  );
}
