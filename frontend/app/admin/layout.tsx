'use client'
import useAuth from "@/lib/auth";

export default function Layout({ children }: { children: React.ReactNode }) {
    useAuth();
    return children;
}