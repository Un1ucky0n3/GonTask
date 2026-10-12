import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { api, type Employee, type Status, type Task } from '../api'
import { useAuth } from '../auth'

const STATUSES: Status[] = ['TODO', 'IN_PROGRESS', 'COMPLETED']
const label = (s: Status) => s.replace('_', ' ').toLowerCase()
interface Draft { id?: number; title: string; description: string; employeeId: string; dueDate: string; status: Status }

export default function Tasks() {
    const { me } = useAuth()
    const isManager = me?.role === 'MANAGER'
    const [tasks, setTasks] = useState<Task[]>([])
    const [employees, setEmployees] = useState<Employee[]>([])
    const [filter, setFilter] = useState<Status | ''>('')
    const [draft, setDraft] = useState<Draft | null>(null)
    const [error, setError] = useState('')

    const load = useCallback(() => {
        api<Task[]>('/api/tasks' + (filter ? `?status=${filter}` : '')).then(setTasks).catch((e) => setError(e.message))
    }, [filter])
    useEffect(load, [load])
    useEffect(() => {
        if (isManager) api<Employee[]>('/api/employees').then(setEmployees).catch((e) => setError(e.message))
    }, [isManager])

    const run = (p: Promise<unknown>) => p.then(() => { setError(''); load() }).catch((e) => setError(e.message))

    function save(e: FormEvent) {
        e.preventDefault()
        if (!draft) return
        const body = {
            title: draft.title, description: draft.description, employeeId: Number(draft.employeeId),
            dueDate: draft.dueDate || null, status: draft.id ? draft.status : undefined,
        }
        run(api(draft.id ? `/api/tasks/${draft.id}` : '/api/tasks', draft.id ? 'PUT' : 'POST', body))
        setDraft(null)
    }

    const edit = (t: Task) => setDraft({
        id: t.id, title: t.title, description: t.description ?? '', employeeId: String(t.employeeId),
        dueDate: t.dueDate?.slice(0, 16) ?? '', status: t.status,
    })

    return (
        <>
            <div className="row">
                <h1>Tasks</h1>
                <span className="spacer" />
                {isManager && (
                    <button className="btn" onClick={() => setDraft({ title: '', description: '', employeeId: String(employees[0]?.id ?? ''), dueDate: '', status: 'TODO' })}>
                        New task
                    </button>
                )}
            </div>

            <div className="tabs">
                {(['', ...STATUSES] as const).map((s) => (
                    <button key={s} className={filter === s ? 'tab active' : 'tab'} onClick={() => setFilter(s)}>
                        {s ? label(s) : 'all'}
                    </button>
                ))}
            </div>

            {error && <p className="error">{error}</p>}
            {tasks.length === 0 && <p className="muted">{isManager ? 'No tasks yet. Create one to get started.' : 'Nothing assigned to you right now.'}</p>}

            {tasks.map((t) => {
                const overdue = t.dueDate && t.status !== 'COMPLETED' && new Date(t.dueDate) < new Date()
                return (
                    <div className="card task" key={t.id}>
                        <div className="grow">
                            <h3>{t.title}</h3>
                            {t.description && <p>{t.description}</p>}
                            <p className="muted small">
                                {isManager ? `Assigned to ${t.employeeName}` : `From ${t.managerName}`}
                                {t.dueDate && <span className={overdue ? 'overdue' : ''}> · due {new Date(t.dueDate).toLocaleString()}</span>}
                            </p>
                        </div>
                        {isManager ? (
                            <div className="actions">
                                <span className={`badge ${t.status}`}>{label(t.status)}</span>
                                <button className="btn ghost" onClick={() => edit(t)}>Edit</button>
                                <button className="btn danger" onClick={() => confirm('Delete this task?') && run(api(`/api/tasks/${t.id}`, 'DELETE'))}>Delete</button>
                            </div>
                        ) : (
                            <select value={t.status} onChange={(e) => run(api(`/api/tasks/${t.id}/status`, 'PATCH', { status: e.target.value }))}>
                                {STATUSES.map((s) => <option key={s} value={s}>{label(s)}</option>)}
                            </select>
                        )}
                    </div>
                )
            })}

            {draft && (
                <div className="overlay" onClick={() => setDraft(null)}>
                    <form className="card modal" onClick={(e) => e.stopPropagation()} onSubmit={save}>
                        <h2>{draft.id ? 'Edit task' : 'New task'}</h2>
                        <label>Title<input value={draft.title} maxLength={100} onChange={(e) => setDraft({ ...draft, title: e.target.value })} required autoFocus /></label>
                        <label>Description<textarea value={draft.description} maxLength={1000} onChange={(e) => setDraft({ ...draft, description: e.target.value })} /></label>
                        <label>Assign to
                            <select value={draft.employeeId} onChange={(e) => setDraft({ ...draft, employeeId: e.target.value })} required>
                                {employees.map((x) => <option key={x.id} value={x.id}>{x.firstName} {x.lastName}</option>)}
                            </select>
                        </label>
                        <label>Due date<input type="datetime-local" value={draft.dueDate} onChange={(e) => setDraft({ ...draft, dueDate: e.target.value })} /></label>
                        {draft.id && (
                            <label>Status
                                <select value={draft.status} onChange={(e) => setDraft({ ...draft, status: e.target.value as Status })}>
                                    {STATUSES.map((s) => <option key={s} value={s}>{label(s)}</option>)}
                                </select>
                            </label>
                        )}
                        <div className="row end">
                            <button type="button" className="btn ghost" onClick={() => setDraft(null)}>Cancel</button>
                            <button className="btn">Save task</button>
                        </div>
                    </form>
                </div>
            )}
        </>
    )
}
