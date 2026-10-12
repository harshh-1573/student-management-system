import React, { useState, useEffect } from 'react';
import { studentApi } from '../../api/studentApi';
import { departmentApi } from '../../api/departmentApi';
import { useAuth } from '../../context/AuthContext';
import { DataTable } from '../../components/common/DataTable';
import { Pagination } from '../../components/common/Pagination';
import { SearchBar } from '../../components/common/SearchBar';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { FormInput } from '../../components/common/FormInput';
import { Plus, Edit2, Trash2, FileText, Mail } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export const StudentsPage = () => {
  const { hasRole } = useAuth();
  const isAdmin = hasRole('ROLE_ADMIN');
  const navigate = useNavigate();

  const [students, setStudents] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState('');
  const [sortBy, setSortBy] = useState('id');
  const [sortDir, setSortDir] = useState('asc');

  // Modals state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [deleteId, setDeleteId] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    fetchStudents();
    fetchDepartments();
  }, [page, search, sortBy, sortDir]);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const res = await studentApi.getAll({
        page,
        size: 10,
        sortBy,
        sortDir,
        search,
      });
      if (res.success && res.data) {
        setStudents(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      toast.error('Failed to load students');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const res = await departmentApi.getAllList();
      if (res.success && res.data) {
        setDepartments(res.data);
      }
    } catch (err) {
      console.error('Failed to load departments:', err);
    }
  };

  const handleOpenAdd = () => {
    setEditingStudent(null);
    reset({
      rollNumber: '',
      name: '',
      email: '',
      phone: '',
      dateOfBirth: '2002-01-01',
      address: '',
      enrollmentYear: 2024,
      departmentId: departments[0]?.id || '',
      password: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (student) => {
    setEditingStudent(student);
    setValue('rollNumber', student.rollNumber);
    setValue('name', student.name);
    setValue('email', student.email);
    setValue('phone', student.phone || '');
    setValue('dateOfBirth', student.dateOfBirth || '');
    setValue('address', student.address || '');
    setValue('enrollmentYear', student.enrollmentYear);
    setValue('departmentId', student.departmentId);
    setValue('password', '');
    setIsModalOpen(true);
  };

  const onSubmit = async (data) => {
    try {
      setActionLoading(true);
      const payload = {
        ...data,
        enrollmentYear: Number(data.enrollmentYear),
        departmentId: Number(data.departmentId),
      };
      if (editingStudent) {
        await studentApi.update(editingStudent.id, payload);
        toast.success('Student updated successfully');
      } else {
        await studentApi.create(payload);
        toast.success('Student created successfully with login credentials');
      }
      setIsModalOpen(false);
      fetchStudents();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save student';
      toast.error(msg);
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      setActionLoading(true);
      await studentApi.delete(deleteId);
      toast.success('Student deleted successfully');
      setDeleteId(null);
      fetchStudents();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete student';
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
      header: 'Roll Number',
      field: 'rollNumber',
      sortable: true,
      render: (row) => (
        <span className="font-mono font-bold text-xs bg-slate-100 text-slate-800 px-2.5 py-1 rounded-md">
          {row.rollNumber}
        </span>
      ),
    },
    {
      header: 'Student Name',
      field: 'name',
      sortable: true,
      render: (row) => (
        <div>
          <p className="font-semibold text-slate-800">{row.name}</p>
          <p className="text-xs text-slate-400 flex items-center space-x-1 mt-0.5">
            <Mail className="w-3 h-3" />
            <span>{row.email}</span>
          </p>
        </div>
      ),
    },
    {
      header: 'Department',
      field: 'departmentName',
      render: (row) => (
        <span className="bg-indigo-50 text-indigo-700 font-medium px-2.5 py-1 rounded-md text-xs">
          {row.departmentName || row.departmentCode}
        </span>
      ),
    },
    {
      header: 'Year',
      field: 'enrollmentYear',
      sortable: true,
      render: (row) => <span className="text-xs font-semibold text-slate-600">{row.enrollmentYear}</span>,
    },
    {
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center space-x-2">
          <button
            onClick={() => navigate(`/students/${row.id}`)}
            className="flex items-center space-x-1 px-2.5 py-1 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 rounded-lg text-xs font-semibold transition-colors"
            title="View Academic Report"
          >
            <FileText className="w-3.5 h-3.5" />
            <span>Report</span>
          </button>
          {isAdmin && (
            <>
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
            </>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Students</h1>
          <p className="text-xs text-slate-500 mt-1">Student roster, profiles, and academic performance</p>
        </div>
        {isAdmin && (
          <button
            onClick={handleOpenAdd}
            className="flex items-center space-x-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-md shadow-indigo-200 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Add Student</span>
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
          placeholder="Search by roll number, name, or email..."
        />
      </div>

      {/* Table */}
      <DataTable
        columns={columns}
        data={students}
        loading={loading}
        onSort={handleSort}
        sortBy={sortBy}
        sortDir={sortDir}
        emptyTitle="No students found"
        emptyDescription="Register your first student to begin."
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
        title={editingStudent ? 'Edit Student' : 'Add New Student'}
        maxWidth="max-w-xl"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <FormInput
              label="Roll Number"
              name="rollNumber"
              placeholder="e.g. STU-2024-001"
              register={register}
              error={errors.rollNumber}
              required
              rules={{ required: 'Roll number is required' }}
            />

            <FormInput
              label="Full Name"
              name="name"
              placeholder="e.g. John Doe"
              register={register}
              error={errors.name}
              required
              rules={{ required: 'Name is required' }}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <FormInput
              label="Email Address"
              name="email"
              type="email"
              placeholder="e.g. john@sms.com"
              register={register}
              error={errors.email}
              required
              rules={{
                required: 'Email is required',
                pattern: { value: /^\S+@\S+$/i, message: 'Invalid email address' },
              }}
            />

            <FormInput
              label="Phone Number"
              name="phone"
              placeholder="+1 555-0199"
              register={register}
              error={errors.phone}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <FormInput
              label="Date of Birth"
              name="dateOfBirth"
              type="date"
              register={register}
              error={errors.dateOfBirth}
            />

            <FormInput
              label="Enrollment Year"
              name="enrollmentYear"
              type="number"
              placeholder="2024"
              register={register}
              error={errors.enrollmentYear}
              required
              rules={{ required: 'Year is required' }}
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
            label="Home Address"
            name="address"
            placeholder="e.g. 123 Elm St, Tech City"
            register={register}
            error={errors.address}
          />

          <FormInput
            label={editingStudent ? 'Change Password (Optional)' : 'Account Password'}
            name="password"
            type="password"
            placeholder={editingStudent ? 'Leave blank to keep unchanged' : 'Default: Student@123'}
            register={register}
            error={errors.password}
          />

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
              {actionLoading ? 'Saving...' : editingStudent ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDeleteConfirm}
        title="Delete Student"
        message="Are you sure you want to delete this student? All course enrollments, attendance, and exam marks will be permanently removed."
        loading={actionLoading}
      />
    </div>
  );
};

export default StudentsPage;
