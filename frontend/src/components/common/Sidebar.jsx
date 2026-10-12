import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  BookOpen,
  Building2,
  UserCheck,
  CalendarCheck,
  Award,
  UserCircle,
  X,
} from 'lucide-react';

export const Sidebar = ({ isOpen, onClose }) => {
  const { user } = useAuth();
  const role = user?.role;

  const adminLinks = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/students', label: 'Students', icon: Users },
    { to: '/teachers', label: 'Teachers', icon: GraduationCap },
    { to: '/courses', label: 'Courses', icon: BookOpen },
    { to: '/departments', label: 'Departments', icon: Building2 },
    { to: '/enrollments', label: 'Enrollments', icon: UserCheck },
    { to: '/attendance', label: 'Attendance', icon: CalendarCheck },
    { to: '/marks', label: 'Marks & Grades', icon: Award },
  ];

  const teacherLinks = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/students', label: 'Students', icon: Users },
    { to: '/courses', label: 'My Courses', icon: BookOpen },
    { to: '/attendance', label: 'Mark Attendance', icon: CalendarCheck },
    { to: '/marks', label: 'Enter Marks', icon: Award },
  ];

  const studentLinks = [
    { to: '/portal', label: 'Student Portal', icon: UserCircle },
  ];

  let links = studentLinks;
  if (role === 'ROLE_ADMIN') links = adminLinks;
  else if (role === 'ROLE_TEACHER') links = teacherLinks;

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 z-40 bg-slate-900/50 backdrop-blur-sm lg:hidden"
        />
      )}

      <aside
        className={`fixed lg:static top-0 bottom-0 left-0 z-50 w-64 bg-white border-r border-slate-100 flex flex-col transition-transform duration-300 ease-in-out ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        <div className="h-16 flex items-center justify-between px-6 border-b border-slate-100">
          <div className="flex items-center space-x-2.5">
            <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white font-bold text-sm shadow-md shadow-indigo-200">
              SM
            </div>
            <span className="font-bold text-slate-800 text-sm tracking-tight">Student Management</span>
          </div>
          <button
            onClick={onClose}
            className="lg:hidden p-1.5 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="flex-1 py-6 px-4 space-y-1.5 overflow-y-auto">
          <p className="px-3 text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2">
            Navigation
          </p>
          {links.map((link) => {
            const Icon = link.icon;
            return (
              <NavLink
                key={link.to}
                to={link.to}
                onClick={onClose}
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                    isActive
                      ? 'bg-indigo-50 text-indigo-700 shadow-sm'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                <Icon className="w-4 h-4" />
                <span>{link.label}</span>
              </NavLink>
            );
          })}
        </div>

        <div className="p-4 border-t border-slate-100">
          <div className="bg-slate-50 rounded-xl p-3 text-center">
            <p className="text-[11px] font-bold text-slate-700">Spring Boot 3 + React</p>
            <p className="text-[10px] text-slate-400 mt-0.5">JWT & Role-based Access</p>
          </div>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
