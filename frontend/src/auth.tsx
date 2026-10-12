import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { api, getToken, setToken, type Me } from './api'

interface Auth {
    me: Me | null
    loading: boolean
    login: (email: string, password: string) => Promise<void>
    logout: () => void
}

const Ctx = createContext<Auth>(null!)
export const useAuth = () => useContext(Ctx)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [me, setMe] = useState<Me | null>(null)
    const [loading, setLoading] = useState(!!getToken())

    useEffect(() => {
        if (!getToken()) return
        api<Me>('/api/me').then(setMe).catch(() => setToken(null)).finally(() => setLoading(false))
    }, [])

    async function login(email: string, password: string) {
        const r = await api<{ token: string }>('/api/auth/login', 'POST', { email, password })
        setToken(r.token)
        setMe(await api<Me>('/api/me'))
    }
    function logout() {
        setToken(null)
        setMe(null)
    }

    return <Ctx.Provider value={{ me, loading, login, logout }}>{children}</Ctx.Provider>
}
