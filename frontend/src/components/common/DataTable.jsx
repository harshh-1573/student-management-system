import React from 'react';
import { Loader } from './Loader';
import { EmptyState } from './EmptyState';
import { ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-react';

export const DataTable = ({
  columns = [],
  data = [],
  loading = false,
  emptyTitle = 'No data available',
  emptyDescription = 'There are no records to display.',
  onSort,
  sortBy,
  sortDir,
}) => {
  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-slate-100 shadow-sm min-h-[300px] flex items-center justify-center">
        <Loader size="lg" text="Loading table data..." />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return <EmptyState title={emptyTitle} description={emptyDescription} />;
  }

  return (
    <div className="bg-white rounded-xl border border-slate-100 shadow-sm overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/80 border-b border-slate-100 text-xs font-semibold text-slate-500 uppercase tracking-wider">
              {columns.map((col, index) => (
                <th
                  key={index}
                  onClick={() => col.sortable && onSort && onSort(col.field)}
                  className={`px-6 py-4 ${
                    col.sortable ? 'cursor-pointer select-none hover:text-slate-800' : ''
                  }`}
                >
                  <div className="flex items-center space-x-1.5">
                    <span>{col.header}</span>
                    {col.sortable && (
                      <span className="text-slate-400">
                        {sortBy === col.field ? (
                          sortDir === 'asc' ? (
                            <ArrowUp className="w-3.5 h-3.5 text-indigo-600" />
                          ) : (
                            <ArrowDown className="w-3.5 h-3.5 text-indigo-600" />
                          )
                        ) : (
                          <ArrowUpDown className="w-3.5 h-3.5" />
                        )}
                      </span>
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-sm">
            {data.map((row, rowIndex) => (
              <tr key={row.id || rowIndex} className="hover:bg-slate-50/60 transition-colors">
                {columns.map((col, colIndex) => (
                  <td key={colIndex} className="px-6 py-4 text-slate-700 whitespace-nowrap">
                    {col.render ? col.render(row) : row[col.field]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DataTable;
