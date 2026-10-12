import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute } from './components/routes/ProtectedRoute';
import { RoleRoute } from './components/routes/RoleRoute';

// Pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import AdminDashboard from './pages/dashboard/AdminDashboard';
import DepartmentsPage from './pages/departments/DepartmentsPage';
import TeachersPage from './pages/teachers/TeachersPage';
import StudentsPage from './pages/students/StudentsPage';
import StudentDetailPage from './pages/students/StudentDetailPage';
import CoursesPage from './pages/courses/CoursesPage';
import EnrollmentPage from './pages/enrollments/EnrollmentPage';
import AttendancePage from './pages/attendance/AttendancePage';
import MarksPage from './pages/marks/MarksPage';
import StudentPortal from './pages/student-portal/StudentPortal';
import NotFound from './pages/NotFound';

function RootRedirect() {
  const { user, isStudent } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (isStudent) return <Navigate to="/portal" replace />;
  return <Navigate to="/dashboard" replace />;
}

export default function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Protected Routes inside AppLayout */}
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<RootRedirect />} />

          {/* Admin & Teacher Dashboard */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/dashboard" element={<AdminDashboard />} />
          </Route>

          {/* Departments */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/departments" element={<DepartmentsPage />} />
          </Route>

          {/* Teachers (Admin only) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN']} />}>
            <Route path="/teachers" element={<TeachersPage />} />
          </Route>

          {/* Students (Admin & Teacher) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/students/:id" element={<StudentDetailPage />} />
          </Route>

          {/* Courses (All authenticated roles) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT']} />}>
            <Route path="/courses" element={<CoursesPage />} />
          </Route>

          {/* Enrollments (Admin & Teacher) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/enrollments" element={<EnrollmentPage />} />
          </Route>

          {/* Attendance (Admin & Teacher) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/attendance" element={<AttendancePage />} />
          </Route>

          {/* Marks & Grades (Admin & Teacher) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']} />}>
            <Route path="/marks" element={<MarksPage />} />
          </Route>

          {/* Student Portal (Student only) */}
          <Route element={<RoleRoute allowedRoles={['ROLE_STUDENT']} />}>
            <Route path="/portal" element={<StudentPortal />} />
            <Route path="/student/portal" element={<Navigate to="/portal" replace />} />
          </Route>
        </Route>
      </Route>

      {/* 404 Fallback */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
