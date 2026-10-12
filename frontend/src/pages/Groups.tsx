import { useEffect, useState, type FormEvent } from 'react'
import { api, type Employee, type Group } from '../api'

export default function Groups() {
    const [groups, setGroups] = useState<Group[]>([])
    const [employees, setEmployees] = useState<Employee[]>([])
    const [name, setName] = useState('')
    const [error, setError] = useState('')

    const load = () => api<Group[]>('/api/groups').then(setGroups).catch((e) => setError(e.message))
    useEffect(() => {
        load()
        api<Employee[]>('/api/employees').then(setEmployees).catch((e) => setError(e.message))
    }, [])

    const run = (p: Promise<unknown>) => p.then(() => { setError(''); load() }).catch((e) => setError(e.message))
    const create = (e: FormEvent) => { e.preventDefault(); run(api('/api/groups', 'POST', { name })); setName('') }

    return (
        <>
            <h1>Groups</h1>
            <form className="card row" onSubmit={create}>
                <input className="grow" placeholder="New group name" value={name} maxLength={100} onChange={(e) => setName(e.target.value)} required />
                <button className="btn">Create group</button>
            </form>
            {error && <p className="error">{error}</p>}
            {groups.length === 0 && <p className="muted">No groups yet.</p>}

            {groups.map((g) => {
                const available = employees.filter((x) => !g.employees.some((m) => m.id === x.id))
                return (
                    <div className="card" key={g.id}>
                        <div className="row">
                            <h3 className="grow">{g.name}</h3>
                            <button className="btn danger" onClick={() => confirm(`Delete group "${g.name}"?`) && run(api(`/api/groups/${g.id}`, 'DELETE'))}>Delete</button>
                        </div>
                        <div className="chips">
                            {g.employees.length === 0 && <span className="muted small">No members yet.</span>}
                            {g.employees.map((m) => (
                                <span className="chip" key={m.id}>
                  {m.firstName} {m.lastName}
                                    <button aria-label={`Remove ${m.firstName}`} onClick={() => run(api(`/api/groups/${g.id}/employees/${m.id}`, 'DELETE'))}>×</button>
                </span>
                            ))}
                        </div>
                        {available.length > 0 && (
                            <select value="" onChange={(e) => e.target.value && run(api(`/api/groups/${g.id}/employees/${e.target.value}`, 'POST'))}>
                                <option value="">Add a member…</option>
                                {available.map((x) => <option key={x.id} value={x.id}>{x.firstName} {x.lastName}</option>)}
                            </select>
                        )}
                    </div>
                )
            })}
        </>
    )
}
