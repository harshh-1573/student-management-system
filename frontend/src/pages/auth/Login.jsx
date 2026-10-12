import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { FormInput } from '../../components/common/FormInput';
import { GraduationCap, LogIn, Sparkles } from 'lucide-react';

export const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    const result = await login(data);
    setLoading(false);
    if (result.success) {
      if (result.user.role === 'ROLE_STUDENT') {
        navigate('/portal');
      } else {
        navigate('/dashboard');
      }
    }
  };

  const handleQuickLogin = (email, password) => {
    setValue('email', email);
    setValue('password', password);
    onSubmit({ email, password });
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100 p-8">
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white flex items-center justify-center mx-auto mb-4 shadow-lg shadow-indigo-200">
            <GraduationCap className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-slate-900 tracking-tight">Welcome Back</h2>
          <p className="text-xs text-slate-500 mt-1">Sign in to Student Management System</p>
        </div>

        {/* Demo Accounts Quick Login */}
        <div className="mb-6 p-3.5 bg-slate-50 rounded-2xl border border-slate-100">
          <div className="flex items-center space-x-1.5 text-xs font-semibold text-slate-600 mb-2">
            <Sparkles className="w-3.5 h-3.5 text-amber-500" />
            <span>1-Click Demo Login</span>
          </div>
          <div className="grid grid-cols-3 gap-2">
            <button
              type="button"
              onClick={() => handleQuickLogin('admin@sms.com', 'Admin@123')}
              className="py-1.5 px-2 bg-purple-50 hover:bg-purple-100 text-purple-700 text-[11px] font-semibold rounded-lg border border-purple-200 transition-all text-center"
            >
              Admin
            </button>
            <button
              type="button"
              onClick={() => handleQuickLogin('alan.turing@sms.com', 'Teacher@123')}
              className="py-1.5 px-2 bg-blue-50 hover:bg-blue-100 text-blue-700 text-[11px] font-semibold rounded-lg border border-blue-200 transition-all text-center"
            >
              Teacher
            </button>
            <button
              type="button"
              onClick={() => handleQuickLogin('john.doe@sms.com', 'Student@123')}
              className="py-1.5 px-2 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 text-[11px] font-semibold rounded-lg border border-emerald-200 transition-all text-center"
            >
              Student
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <FormInput
            label="Email Address"
            name="email"
            type="email"
            placeholder="e.g. admin@sms.com"
            register={register}
            error={errors.email}
            required
            rules={{
              required: 'Email is required',
              pattern: { value: /^\S+@\S+$/i, message: 'Invalid email address' },
            }}
          />

          <FormInput
            label="Password"
            name="password"
            type="password"
            placeholder="••••••••"
            register={register}
            error={errors.password}
            required
            rules={{ required: 'Password is required' }}
          />

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-2 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm rounded-xl shadow-lg shadow-indigo-200 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
          >
            <LogIn className="w-4 h-4" />
            <span>{loading ? 'Authenticating...' : 'Sign In'}</span>
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-slate-500">
          Don't have an account?{' '}
          <Link to="/register" className="font-semibold text-indigo-600 hover:underline">
            Register here
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
