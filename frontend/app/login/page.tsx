'use client'
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function Home() {
  const [email, setEmail] = useState('alice@123.net');
  const [password, setPassword] = useState('123456');
  const router = useRouter();

  const login = () => {
    fetch('/api/v1/user/signIn', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    }).then(res => res.json())
      .then(data => {
        localStorage.setItem('session', JSON.stringify(data));
        console.log('login:', data)
        router.push('/');
      }).catch(err => {
        alert('login failed')
        console.log(err)
      })
  }

  return <div className="flex flex-col gap-4 max-w-[20vw] mx-auto min-h-[60vh] justify-center items-center">
    <h3 className="text-2xl font-semibold">Login</h3>
    <Input
      aria-label="Email"
      type="email"
      placeholder="Please enter your email"
      defaultValue={email}
      onChange={(e) => setEmail(e.target.value)}
    />
    <Input
      aria-label="Password"
      type="password"
      placeholder="Please enter your password"
      defaultValue={password}
      onChange={(e) => setPassword(e.target.value)}
    />
    <Link
      className="opacity-50 text-blue-600"
      href="/login/signUp">Don't have an account? Sign up</Link>
    <Button
      type='submit'
      className="w-[100%]"
      onClick={login}
    >Login</Button>
  </div >;
}
