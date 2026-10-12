import React, { useState, useEffect } from 'react';
import { teacherApi } from '../../api/teacherApi';
import { departmentApi } from '../../api/departmentApi';
import { useAuth } from '../../context/AuthContext';
import { DataTable } from '../../components/common/DataTable';
import { Pagination } from '../../components/common/Pagination';
import { SearchBar } from '../../components/common/SearchBar';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { FormInput } from '../../components/common/FormInput';
import { Plus, Edit2, Trash2, Mail, Phone } from 'lucide-react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export const TeachersPage = () => {
  const { hasRole } = useAuth();
  const isAdmin = hasRole('ROLE_ADMIN');

  const [teachers, setTeachers] = useState([]);
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
  const [editingTeacher, setEditingTeacher] = useState(null);
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
    fetchTeachers();
    fetchDepartments();
  }, [page, search, sortBy, sortDir]);

  const fetchTeachers = async () => {
    try {
      setLoading(true);
      const res = await teacherApi.getAll({
        page,
        size: 10,
        sortBy,
        sortDir,
        search,
      });
      if (res.success && res.data) {
        setTeachers(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      toast.error('Failed to load teachers');
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
      console.error('Failed to load departments dropdown:', err);
    }
  };

  const handleOpenAdd = () => {
    setEditingTeacher(null);
    reset({
      name: '',
      email: '',
      phone: '',
      qualification: '',
      departmentId: departments[0]?.id || '',
      password: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (teacher) => {
    setEditingTeacher(teacher);
    setValue('name', teacher.name);
    setValue('email', teacher.email);
    setValue('phone', teacher.phone || '');
    setValue('qualification', teacher.qualification || '');
    setValue('departmentId', teacher.departmentId);
    setValue('password', '');
    setIsModalOpen(true);
  };

  const onSubmit = async (data) => {
    try {
      setActionLoading(true);
      const payload = {
        ...data,
        departmentId: Number(data.departmentId),
      };
      if (editingTeacher) {
        await teacherApi.update(editingTeacher.id, payload);
        toast.success('Teacher updated successfully');
      } else {
        await teacherApi.create(payload);
        toast.success('Teacher created successfully with login credentials');
      }
      setIsModalOpen(false);
      fetchTeachers();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save teacher';
      toast.error(msg);
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      setActionLoading(true);
      await teacherApi.delete(deleteId);
      toast.success('Teacher deleted successfully');
      setDeleteId(null);
      fetchTeachers();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete teacher';
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
      header: 'Faculty Member',
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
        <span className="bg-slate-100 text-slate-700 font-medium px-2.5 py-1 rounded-md text-xs">
          {row.departmentName || row.departmentCode}
        </span>
      ),
    },
    {
      header: 'Qualification',
      field: 'qualification',
      render: (row) => <span className="text-xs text-slate-600">{row.qualification || '-'}</span>,
    },
    {
      header: 'Phone',
      field: 'phone',
      render: (row) => (
        <span className="text-xs text-slate-500 flex items-center space-x-1">
          <Phone className="w-3 h-3" />
          <span>{row.phone || '-'}</span>
        </span>
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
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Teachers</h1>
          <p className="text-xs text-slate-500 mt-1">Manage academic professors and teaching staff</p>
        </div>
        {isAdmin && (
          <button
            onClick={handleOpenAdd}
            className="flex items-center space-x-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-md shadow-indigo-200 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Add Teacher</span>
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
          placeholder="Search by name or email..."
        />
      </div>

      {/* Table */}
      <DataTable
        columns={columns}
        data={teachers}
        loading={loading}
        onSort={handleSort}
        sortBy={sortBy}
        sortDir={sortDir}
        emptyTitle="No teachers found"
        emptyDescription="Create your first faculty member."
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
        title={editingTeacher ? 'Edit Teacher' : 'Add New Teacher'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <FormInput
            label="Full Name"
            name="name"
            placeholder="e.g. Prof. Alan Turing"
            register={register}
            error={errors.name}
            required
            rules={{ required: 'Name is required' }}
          />

          <FormInput
            label="Email Address"
            name="email"
            type="email"
            placeholder="e.g. alan@sms.com"
            register={register}
            error={errors.email}
            required
            rules={{
              required: 'Email is required',
              pattern: { value: /^\S+@\S+$/i, message: 'Invalid email address' },
            }}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <FormInput
              label="Phone Number"
              name="phone"
              placeholder="+1 555-0101"
              register={register}
              error={errors.phone}
            />

            <FormInput
              label="Qualification"
              name="qualification"
              placeholder="e.g. Ph.D. in Computer Science"
              register={register}
              error={errors.qualification}
            />
          </div>

          <FormInput
            label="Department"
            name="departmentId"
            type="select"
            register={register}
            error={errors.departmentId}
            required
            rules={{ required: 'Please select a department' }}
          >
            {departments.map((dept) => (
              <option key={dept.id} value={dept.id}>
                {dept.name} ({dept.code})
              </option>
            ))}
          </FormInput>

          <FormInput
            label={editingTeacher ? 'Change Password (Optional)' : 'Account Password'}
            name="password"
            type="password"
            placeholder={editingTeacher ? 'Leave blank to keep unchanged' : 'Default: Teacher@123'}
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
              {actionLoading ? 'Saving...' : editingTeacher ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDeleteConfirm}
        title="Delete Teacher"
        message="Are you sure you want to delete this teacher profile? Course assignments will be cleared."
        loading={actionLoading}
      />
    </div>
  );
};

export default TeachersPage;
