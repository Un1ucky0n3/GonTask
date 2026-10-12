import { useEffect, useState, type FormEvent } from 'react'
import { api, type Employee } from '../api'

const empty = { firstName: '', lastName: '', email: '', password: '' }

export default function Employees() {
    const [list, setList] = useState<Employee[]>([])
    const [form, setForm] = useState(empty)
    const [error, setError] = useState('')

    const load = () => api<Employee[]>('/api/employees').then(setList).catch((e) => setError(e.message))
    useEffect(() => { load() }, [])

    async function add(e: FormEvent) {
        e.preventDefault()
        try {
            await api('/api/employees', 'POST', form)
            setForm(empty)
            setError('')
            load()
        } catch (err) {
            setError((err as Error).message)
        }
    }
    const set = (k: keyof typeof empty) => (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value })

    return (
        <>
            <h1>Employees</h1>
            <form className="card form-grid" onSubmit={add}>
                <label>First name<input value={form.firstName} onChange={set('firstName')} maxLength={50} required /></label>
                <label>Last name<input value={form.lastName} onChange={set('lastName')} maxLength={50} required /></label>
                <label>Email<input type="email" value={form.email} onChange={set('email')} required /></label>
                <label>Password (8+ characters)<input type="password" value={form.password} onChange={set('password')} minLength={8} required /></label>
                <button className="btn">Add employee</button>
            </form>
            {error && <p className="error">{error}</p>}
            <div className="card">
                {list.length === 0 && <p className="muted">No employees yet.</p>}
                {list.map((x) => (
                    <div className="list-row" key={x.id}>
                        <strong>{x.firstName} {x.lastName}</strong>
                        <span className="muted">{x.email}</span>
                    </div>
                ))}
            </div>
        </>
    )
}
