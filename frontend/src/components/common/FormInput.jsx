import React from 'react';

export const FormInput = ({
  label,
  name,
  type = 'text',
  placeholder,
  register,
  error,
  required = false,
  disabled = false,
  children,
  className = '',
  ...props
}) => {
  return (
    <div className={`space-y-1.5 ${className}`}>
      {label && (
        <label className="block text-xs font-semibold text-slate-700 tracking-wide uppercase">
          {label} {required && <span className="text-rose-500">*</span>}
        </label>
      )}

      {type === 'select' ? (
        <select
          {...(register ? register(name) : {})}
          disabled={disabled}
          className={`w-full px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 disabled:bg-slate-50 disabled:text-slate-400 ${
            error ? 'border-rose-400 focus:border-rose-500 focus:ring-rose-500/20' : 'border-slate-200'
          }`}
          {...props}
        >
          {children}
        </select>
      ) : type === 'textarea' ? (
        <textarea
          {...(register ? register(name) : {})}
          placeholder={placeholder}
          disabled={disabled}
          rows={3}
          className={`w-full px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 disabled:bg-slate-50 disabled:text-slate-400 ${
            error ? 'border-rose-400 focus:border-rose-500 focus:ring-rose-500/20' : 'border-slate-200'
          }`}
          {...props}
        />
      ) : (
        <input
          type={type}
          {...(register ? register(name) : {})}
          placeholder={placeholder}
          disabled={disabled}
          className={`w-full px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 disabled:bg-slate-50 disabled:text-slate-400 ${
            error ? 'border-rose-400 focus:border-rose-500 focus:ring-rose-500/20' : 'border-slate-200'
          }`}
          {...props}
        />
      )}

      {error && <p className="text-xs text-rose-500 mt-1 font-medium">{error.message || error}</p>}
    </div>
  );
};

export default FormInput;
