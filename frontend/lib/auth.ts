import { useEffect } from "react";

export default function useAuth() {

    useEffect(() => {
        const sessionJson = localStorage.getItem('session');
        if (!sessionJson) {
            window.location.href = '/login';
        }
        const session = JSON.parse(sessionJson as string);
        if (!session) {
            localStorage.removeItem('session');
            window.location.href = '/login';
        }
        fetch('/api/v1/user/auth', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${session.token}`,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                console.log('auth:', data);
                if (!data) {
                    localStorage.removeItem('session');
                    window.location.href = '/login';
                }
            })
            .catch((err) => {
                console.log(err);
                localStorage.removeItem('session');
                window.location.href = '/login';
            });
    }, []);
}