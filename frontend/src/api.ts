const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export type Role = 'MANAGER' | 'EMPLOYEE'
export type Status = 'TODO' | 'IN_PROGRESS' | 'COMPLETED'
export interface Me { accountId: number; email: string; role: Role; profileId: number; firstName: string; lastName: string }
export interface Employee { id: number; email: string; firstName: string; lastName: string }
export interface Task { id: number; title: string; description: string | null; status: Status; employeeId: number; employeeName: string; managerName: string; dueDate: string | null }
export interface Group { id: number; name: string; employees: Employee[] }

export const getToken = () => localStorage.getItem('token')
export const setToken = (t: string | null) =>
    t ? localStorage.setItem('token', t) : localStorage.removeItem('token')

export async function api<T = void>(path: string, method = 'GET', body?: unknown): Promise<T> {
    const token = getToken()
    const res = await fetch(BASE + path, {
        method,
        headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
        body: body === undefined ? undefined : JSON.stringify(body),
    })
    if (res.status === 401 && token && !path.startsWith('/api/auth')) {
        setToken(null)
        window.location.href = '/login'
        throw new Error('Session expired. Please log in again.')
    }
    const text = await res.text()
    const data = text ? JSON.parse(text) : undefined
    if (!res.ok) throw new Error(data?.message ?? `Request failed (${res.status})`)
    return data as T
}
