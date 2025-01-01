'use client'
import { useEffect } from 'react'

function Admin() {
    useEffect(() => {
        window.location.href = '/admin/info'
    })
    return (
        <div>Admin</div>
    )
}

export default Admin