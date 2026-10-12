import React, { useState, useEffect } from 'react';
import { departmentApi } from '../../api/departmentApi';
import { useAuth } from '../../context/AuthContext';
import { DataTable } from '../../components/common/DataTable';
import { Pagination } from '../../components/common/Pagination';
import { SearchBar } from '../../components/common/SearchBar';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { FormInput } from '../../components/common/FormInput';
import { Plus, Edit2, Trash2, Building2 } from 'lucide-react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export const DepartmentsPage = () => {
  const { hasRole } = useAuth();
  const isAdmin = hasRole('ROLE_ADMIN');

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
  const [editingDept, setEditingDept] = useState(null);
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
    fetchDepartments();
  }, [page, search, sortBy, sortDir]);

  const fetchDepartments = async () => {
    try {
      setLoading(true);
      const res = await departmentApi.getAll({
        page,
        size: 10,
        sortBy,
        sortDir,
        search,
      });
      if (res.success && res.data) {
        setDepartments(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      toast.error('Failed to load departments');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAdd = () => {
    setEditingDept(null);
    reset({ name: '', code: '', description: '' });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (dept) => {
    setEditingDept(dept);
    setValue('name', dept.name);
    setValue('code', dept.code);
    setValue('description', dept.description || '');
    setIsModalOpen(true);
  };

  const onSubmit = async (data) => {
    try {
      setActionLoading(true);
      if (editingDept) {
        await departmentApi.update(editingDept.id, data);
        toast.success('Department updated successfully');
      } else {
        await departmentApi.create(data);
        toast.success('Department created successfully');
      }
      setIsModalOpen(false);
      fetchDepartments();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save department';
      toast.error(msg);
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      setActionLoading(true);
      await departmentApi.delete(deleteId);
      toast.success('Department deleted successfully');
      setDeleteId(null);
      fetchDepartments();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete department';
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
        <span className="font-bold text-indigo-600 bg-indigo-50 px-2.5 py-1 rounded-md text-xs">
          {row.code}
        </span>
      ),
    },
    {
      header: 'Department Name',
      field: 'name',
      sortable: true,
      render: (row) => <span className="font-semibold text-slate-800">{row.name}</span>,
    },
    {
      header: 'Description',
      field: 'description',
      render: (row) => <span className="text-slate-500 text-xs">{row.description || '-'}</span>,
    },
    {
      header: 'Students',
      field: 'studentCount',
      render: (row) => (
        <span className="text-xs font-semibold text-slate-600">{row.studentCount} enrolled</span>
      ),
    },
    {
      header: 'Courses',
      field: 'courseCount',
      render: (row) => (
        <span className="text-xs font-semibold text-slate-600">{row.courseCount} offered</span>
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
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Departments</h1>
          <p className="text-xs text-slate-500 mt-1">Manage university faculties and departments</p>
        </div>
        {isAdmin && (
          <button
            onClick={handleOpenAdd}
            className="flex items-center space-x-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-md shadow-indigo-200 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Add Department</span>
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
          placeholder="Search by code or name..."
        />
      </div>

      {/* Table */}
      <DataTable
        columns={columns}
        data={departments}
        loading={loading}
        onSort={handleSort}
        sortBy={sortBy}
        sortDir={sortDir}
        emptyTitle="No departments found"
        emptyDescription="Get started by creating a new academic department."
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
        title={editingDept ? 'Edit Department' : 'Add New Department'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <FormInput
            label="Department Name"
            name="name"
            placeholder="e.g. Mechanical Engineering"
            register={register}
            error={errors.name}
            required
            rules={{ required: 'Department name is required' }}
          />

          <FormInput
            label="Department Code"
            name="code"
            placeholder="e.g. MECH"
            register={register}
            error={errors.code}
            required
            rules={{ required: 'Department code is required' }}
          />

          <FormInput
            label="Description"
            name="description"
            type="textarea"
            placeholder="Brief overview of department offerings..."
            register={register}
            error={errors.description}
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
              {actionLoading ? 'Saving...' : editingDept ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDeleteConfirm}
        title="Delete Department"
        message="Are you sure you want to delete this department? All associated courses and allocations might be affected."
        loading={actionLoading}
      />
    </div>
  );
};

export default DepartmentsPage;
