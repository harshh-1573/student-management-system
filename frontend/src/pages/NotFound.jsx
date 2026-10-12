import React from 'react';
import { Link } from 'react-router-dom';
import { Home, AlertTriangle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function NotFound() {
  const { isStudent } = useAuth();
  const homePath = isStudent ? '/student/portal' : '/dashboard';

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6 text-center">
      <div className="w-20 h-20 bg-indigo-50 border border-indigo-100 rounded-2xl flex items-center justify-center mb-6 shadow-sm">
        <AlertTriangle className="w-10 h-10 text-indigo-600" />
      </div>
      <h1 className="text-4xl font-black text-slate-800 tracking-tight mb-2">404 - Page Not Found</h1>
      <p className="text-slate-500 max-w-md mb-8 text-sm">
        The page you are looking for does not exist, has been removed, or you don't have authorization to view it.
      </p>
      <Link
        to={homePath}
        className="inline-flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium px-5 py-2.5 rounded-lg shadow-sm transition-colors text-sm"
      >
        <Home className="w-4 h-4" />
        <span>Return to Dashboard</span>
      </Link>
    </div>
  );
}
