import React, { useState, useEffect } from 'react';
import { courseApi } from '../../api/courseApi';
import { departmentApi } from '../../api/departmentApi';
import { teacherApi } from '../../api/teacherApi';
import { enrollmentApi } from '../../api/enrollmentApi';
import { useAuth } from '../../context/AuthContext';
import { DataTable } from '../../components/common/DataTable';
import { Pagination } from '../../components/common/Pagination';
import { SearchBar } from '../../components/common/SearchBar';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { FormInput } from '../../components/common/FormInput';
import { Plus, Edit2, Trash2, Users, BookOpen } from 'lucide-react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export const CoursesPage = () => {
  const { hasRole, user } = useAuth();
  const isAdmin = hasRole('ROLE_ADMIN');
  const isTeacher = hasRole('ROLE_TEACHER');

  const [courses, setCourses] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState('');
  const [sortBy, setSortBy] = useState('id');
  const [sortDir, setSortDir] = useState('asc');

  // Modals state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  // Enrolled students viewer modal
  const [viewCourse, setViewCourse] = useState(null);
  const [enrolledStudents, setEnrolledStudents] = useState([]);
  const [enrollmentLoading, setEnrollmentLoading] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    fetchCourses();
    fetchDepartments();
    fetchTeachers();
  }, [page, search, sortBy, sortDir]);

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const res = await courseApi.getAll({
        page,
        size: 10,
        sortBy,
        sortDir,
        search,
      });
      if (res.success && res.data) {
        setCourses(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      toast.error('Failed to load courses');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const res = await departmentApi.getAllList();
      if (res.success && res.data) setDepartments(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchTeachers = async () => {
    try {
      const res = await teacherApi.getAllList();
      if (res.success && res.data) setTeachers(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleOpenAdd = () => {
    setEditingCourse(null);
    reset({
      code: '',
      name: '',
      credits: 4,
      semester: 'Semester 1',
      departmentId: departments[0]?.id || '',
      teacherId: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (course) => {
    setEditingCourse(course);
    setValue('code', course.code);
    setValue('name', course.name);
    setValue('credits', course.credits);
    setValue('semester', course.semester);
    setValue('departmentId', course.departmentId);
    setValue('teacherId', course.teacherId || '');
    setIsModalOpen(true);
  };

  const handleViewEnrolled = async (course) => {
    setViewCourse(course);
    try {
      setEnrollmentLoading(true);
      const res = await enrollmentApi.getByCourse(course.id, { page: 0, size: 50 });
      if (res.success && res.data) {
        setEnrolledStudents(res.data.content);
      }
    } catch (err) {
      toast.error('Failed to load enrolled students');
    } finally {
      setEnrollmentLoading(false);
    }
  };

  const onSubmit = async (data) => {
    try {
      setActionLoading(true);
      const payload = {
        ...data,
        credits: Number(data.credits),
        departmentId: Number(data.departmentId),
        teacherId: data.teacherId ? Number(data.teacherId) : null,
      };

      if (editingCourse) {
        await courseApi.update(editingCourse.id, payload);
        toast.success('Course updated successfully');
      } else {
        await courseApi.create(payload);
        toast.success('Course created successfully');
      }
      setIsModalOpen(false);
      fetchCourses();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save course';
      toast.error(msg);
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      setActionLoading(true);
      await courseApi.delete(deleteId);
      toast.success('Course deleted successfully');
      setDeleteId(null);
      fetchCourses();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete course';
      toast.error(msg);
    } finally {
      setActionLoading(false);
    }
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortDir('asc');
    }
  };

  const columns = [
    {
      header: 'Code',
      field: 'code',
      sortable: true,
      render: (row) => (
        <span className="font-mono font-bold text-xs bg-indigo-50 text-indigo-700 px-2.5 py-1 rounded-md">
          {row.code}
        </span>
      ),
    },
    {
      header: 'Course Name',
      field: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <p className="font-semibold text-slate-800">{row.name}</p>
          <p className="text-xs text-slate-400 mt-0.5">{row.semester}</p>
        </div>
      ),
    },
    {
      header: 'Department',
      field: 'departmentName',
      render: (row) => (
        <span className="bg-slate-100 text-slate-700 font-medium px-2.5 py-1 rounded-md text-xs">
          {row.departmentName}
        </span>
      ),
    },
    {
      header: 'Credits',
      field: 'credits',
      sortable: true,
      render: (row) => (
        <span className="text-xs font-bold text-slate-700">{row.credits} Cr</span>
      ),
    },
    {
      header: 'Assigned Instructor',
      field: 'teacherName',
      render: (row) => (
        <span className="text-xs text-slate-600 font-medium">
          {row.teacherName || <span className="text-slate-400 italic">Not Assigned</span>}
        </span>
      ),
    },
    {
      header: 'Enrolled',
      field: 'enrolledStudentCount',
      render: (row) => (
        <button
          onClick={() => handleViewEnrolled(row)}
          className="flex items-center space-x-1.5 px-2.5 py-1 bg-slate-50 hover:bg-slate-100 text-slate-700 rounded-lg text-xs font-semibold border border-slate-200 transition-colors"
        >
          <Users className="w-3.5 h-3.5 text-indigo-600" />
          <span>{row.enrolledStudentCount} Students</span>
        </button>
      ),
    },
    ...(isAdmin
      ? [
          {
            header: 'Actions',
            render: (row) => (
              <div className="flex items-center space-x-2">
                <button
                  onClick={() => handleOpenEdit(row)}
                  className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                  title="Edit"
                >
                  <Edit2 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setDeleteId(row.id)}
                  className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                  title="Delete"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ),
          },
        ]
      : []),
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Courses</h1>
          <p className="text-xs text-slate-500 mt-1">Course catalog, faculty assignments, and registrations</p>
        </div>
        {isAdmin && (
          <button
            onClick={handleOpenAdd}
            className="flex items-center space-x-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-md shadow-indigo-200 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Add Course</span>
          </button>
        )}
      </div>

      {/* Filter / Search */}
      <div className="flex items-center justify-between gap-4">
        <SearchBar
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(0);
          }}
          placeholder="Search by code or title..."
        />
      </div>

      {/* Table */}
      <DataTable
        columns={columns}
        data={courses}
        loading={loading}
        onSort={handleSort}
        sortBy={sortBy}
        sortDir={sortDir}
        emptyTitle="No courses found"
        emptyDescription="Create a new course offering to get started."
      />

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={10}
        onPageChange={setPage}
      />

      {/* Add / Edit Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCourse ? 'Edit Course' : 'Add New Course'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <FormInput
              label="Course Code"
              name="code"
              placeholder="e.g. CS101"
              register={register}
              error={errors.code}
              required
              rules={{ required: 'Course code is required' }}
            />

            <FormInput
              label="Credits"
              name="credits"
              type="number"
              placeholder="4"
              register={register}
              error={errors.credits}
              required
              rules={{
                required: 'Credits are required',
                min: { value: 1, message: 'Min 1 credit' },
                max: { value: 10, message: 'Max 10 credits' },
              }}
            />
          </div>

          <FormInput
            label="Course Name"
            name="name"
            placeholder="e.g. Data Structures & Algorithms"
            register={register}
            error={errors.name}
            required
            rules={{ required: 'Course name is required' }}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <FormInput
              label="Semester"
              name="semester"
              placeholder="e.g. Semester 3"
              register={register}
              error={errors.semester}
              required
              rules={{ required: 'Semester is required' }}
            />

            <FormInput
              label="Department"
              name="departmentId"
              type="select"
              register={register}
              error={errors.departmentId}
              required
              rules={{ required: 'Department is required' }}
            >
              {departments.map((dept) => (
                <option key={dept.id} value={dept.id}>
                  {dept.name} ({dept.code})
                </option>
              ))}
            </FormInput>
          </div>

          <FormInput
            label="Assigned Teacher (Optional)"
            name="teacherId"
            type="select"
            register={register}
            error={errors.teacherId}
          >
            <option value="">-- No Teacher Assigned --</option>
            {teachers.map((teacher) => (
              <option key={teacher.id} value={teacher.id}>
                {teacher.name} ({teacher.departmentCode})
              </option>
            ))}
          </FormInput>

          <div className="flex items-center justify-end space-x-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-700 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-all disabled:opacity-50"
            >
              {actionLoading ? 'Saving...' : editingCourse ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Enrolled Students Modal */}
      <Modal
        isOpen={!!viewCourse}
        onClose={() => setViewCourse(null)}
        title={`Enrolled Students: ${viewCourse?.code} - ${viewCourse?.name}`}
        maxWidth="max-w-2xl"
      >
        <div className="space-y-4">
          {enrollmentLoading ? (
            <p className="text-center py-6 text-xs text-slate-400">Loading enrolled students...</p>
          ) : enrolledStudents.length > 0 ? (
            <div className="divide-y divide-slate-100 max-h-96 overflow-y-auto">
              {enrolledStudents.map((enrollment) => (
                <div key={enrollment.id} className="py-3 flex items-center justify-between">
                  <div>
                    <p className="font-semibold text-sm text-slate-800">{enrollment.studentName}</p>
                    <p className="text-xs text-slate-400">
                      Roll: {enrollment.studentRollNumber} &bull; {enrollment.studentEmail}
                    </p>
                  </div>
                  <span className="text-xs font-medium px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200">
                    {enrollment.status}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center py-6 text-xs text-slate-500">
              No students currently enrolled in this course.
            </p>
          )}
          <div className="flex justify-end pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setViewCourse(null)}
              className="px-4 py-2 text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg"
            >
              Close
            </button>
          </div>
        </div>
      </Modal>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDeleteConfirm}
        title="Delete Course"
        message="Are you sure you want to delete this course? All associated student enrollments will be deleted."
        loading={actionLoading}
      />
    </div>
  );
};

export default CoursesPage;
