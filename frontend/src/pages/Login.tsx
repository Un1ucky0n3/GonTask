import { useState, type FormEvent } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

export default function Login() {
    const { me, login } = useAuth()
    const navigate = useNavigate()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    if (me) return <Navigate to="/tasks" replace />

    async function submit(e: FormEvent) {
        e.preventDefault()
        try {
            await login(email, password)
            navigate('/tasks')
        } catch (err) {
            setError((err as Error).message)
        }
    }

    return (
        <form className="card login" onSubmit={submit}>
            <h1>Log in to GonTask</h1>
            {error && <p className="error">{error}</p>}
            <label>Email<input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required autoFocus /></label>
            <label>Password<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required /></label>
            <button className="btn">Log in</button>
        </form>
    )
}
