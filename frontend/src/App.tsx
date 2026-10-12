import { BrowserRouter, Navigate, NavLink, Outlet, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth'
import type { Role } from './api'
import Login from './pages/Login'
import Tasks from './pages/Tasks'
import Employees from './pages/Employees'
import Groups from './pages/Groups'

function Protected({ role }: { role?: Role }) {
  const { me, loading } = useAuth()
  if (loading) return <p className="muted center">Loading…</p>
  if (!me) return <Navigate to="/login" replace />
  if (role && me.role !== role) return <Navigate to="/tasks" replace />
  return <Outlet />
}

function Layout() {
  const { me, logout } = useAuth()
  return (
      <>
        <header className="topbar">
          <strong className="brand">GonTask</strong>
          <nav>
            <NavLink to="/tasks">Tasks</NavLink>
            {me?.role === 'MANAGER' && <NavLink to="/employees">Employees</NavLink>}
            {me?.role === 'MANAGER' && <NavLink to="/groups">Groups</NavLink>}
          </nav>
          <span className="spacer" />
          <span className="muted">{me?.firstName} {me?.lastName}</span>
          <button className="btn ghost" onClick={logout}>Log out</button>
        </header>
        <main className="page"><Outlet /></main>
      </>
  )
}

export default function App() {
  return (
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route element={<Protected />}>
              <Route element={<Layout />}>
                <Route path="/tasks" element={<Tasks />} />
                <Route element={<Protected role="MANAGER" />}>
                  <Route path="/employees" element={<Employees />} />
                  <Route path="/groups" element={<Groups />} />
                </Route>
              </Route>
            </Route>
            <Route path="*" element={<Navigate to="/tasks" replace />} />
          </Routes>
        </AuthProvider>
      </BrowserRouter>
  )
}
