import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

export const Pagination = ({ currentPage, totalPages, totalElements, pageSize, onPageChange }) => {
  if (totalPages <= 1) return null;

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-4 px-6 border-t border-slate-100 bg-white">
      <p className="text-xs text-slate-500">
        Showing <span className="font-semibold text-slate-700">{currentPage * pageSize + 1}</span> to{' '}
        <span className="font-semibold text-slate-700">
          {Math.min((currentPage + 1) * pageSize, totalElements)}
        </span>{' '}
        of <span className="font-semibold text-slate-700">{totalElements}</span> results
      </p>

      <div className="flex items-center space-x-1">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="p-2 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
        >
          <ChevronLeft className="w-4 h-4" />
        </button>

        {Array.from({ length: totalPages }, (_, idx) => {
          // Show up to 5 page numbers cleanly
          if (
            idx === 0 ||
            idx === totalPages - 1 ||
            (idx >= currentPage - 1 && idx <= currentPage + 1)
          ) {
            return (
              <button
                key={idx}
                onClick={() => onPageChange(idx)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  currentPage === idx
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                {idx + 1}
              </button>
            );
          } else if (idx === currentPage - 2 || idx === currentPage + 2) {
            return (
              <span key={idx} className="px-1 text-slate-400 text-xs">
                ...
              </span>
            );
          }
          return null;
        })}

        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages - 1}
          className="p-2 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};

export default Pagination;
