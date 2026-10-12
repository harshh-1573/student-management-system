import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User, Menu, Shield } from 'lucide-react';

export const Navbar = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();

  const getRoleBadge = (role) => {
    switch (role) {
      case 'ROLE_ADMIN':
        return 'bg-purple-100 text-purple-700 border-purple-200';
      case 'ROLE_TEACHER':
        return 'bg-blue-100 text-blue-700 border-blue-200';
      case 'ROLE_STUDENT':
        return 'bg-emerald-100 text-emerald-700 border-emerald-200';
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  const getRoleLabel = (role) => {
    if (!role) return '';
    return role.replace('ROLE_', '');
  };

  return (
    <header className="h-16 bg-white border-b border-slate-100 sticky top-0 z-30 flex items-center justify-between px-4 sm:px-6">
      <div className="flex items-center space-x-3">
        <button
          onClick={onToggleSidebar}
          className="lg:hidden p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors"
        >
          <Menu className="w-5 h-5" />
        </button>
        <div className="flex items-center space-x-2 text-indigo-600 font-bold text-lg">
          <Shield className="w-6 h-6" />
          <span className="hidden sm:inline">SMS Portal</span>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        {user && (
          <div className="flex items-center space-x-3">
            <div className="text-right hidden sm:block">
              <p className="text-xs font-semibold text-slate-800">{user.fullName}</p>
              <p className="text-[10px] text-slate-400">{user.email}</p>
            </div>
            <span
              className={`text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full border ${getRoleBadge(
                user.role
              )}`}
            >
              {getRoleLabel(user.role)}
            </span>
          </div>
        )}

        <button
          onClick={() => logout(true)}
          className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-slate-600 hover:text-rose-600 hover:bg-rose-50 border border-slate-200 transition-all"
          title="Sign out"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;
