'use client'
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function SignUp() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [username, setUsername] = useState('');
    const router = useRouter();

    const signUp = () => {
        fetch('/api/v1/user/signUp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ user: { email, password }, userInfo: { username, avatar: null, email } })
        }).then(res => res.json())
            .then(data => {
                localStorage.setItem('session', JSON.stringify(data));
                console.log('signUp:', data)
                router.push('/');
            }).catch(err => {
                alert('sign up failed')
                console.log(err)
            })
    }

    return <div className="flex flex-col gap-4 max-w-[20vw] mx-auto min-h-[60vh] justify-center items-center">
        <h3 className="text-2xl font-semibold">Sign Up</h3>
        <Input
            aria-label="Email"
            type="email"
            placeholder="Please enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
        />
        <Input
            aria-label="Username"
            type="text"
            placeholder="Please enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
        />
        <Input
            aria-label="Password"
            type="password"
            placeholder="Please enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
        />
        <Input
            aria-label="Password Confirm"
            type="password"
            placeholder="Please confirm your password"
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
        />
        <Link
            className="opacity-50 text-blue-600"
            href="/login">Already have an account? Login</Link>
        <Button
            className="w-[100%]"
            onClick={signUp}
        >Sign Up</Button>
    </div >
}
