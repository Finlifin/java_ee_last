'use client'

import Link from "next/link"
import { UserHomeIcon } from "../icons/user-home-icon"
import { useUserInfoStore } from "@/stores/user-info-store"
import { useState } from "react"
import { useRouter } from "next/navigation"


export function UserHome() {
    let [isPopupOpen, setIsPopupOpen] = useState(false)
    let { userInfo, setUserInfo } = useUserInfoStore()
    let router = useRouter()

    const onHover = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
        setIsPopupOpen(true)
    }

    const onLogout = () => {
        setUserInfo(null)
        localStorage.removeItem("session")
        router.push("/login")
    }

    return (
        <div
            className={"mt-0.5 size-8 cursor-pointer items-center justify-center fill-none transition-transform hover:scale-105"}
            onMouseEnter={onHover}
            onMouseLeave={() => setIsPopupOpen(false)}
        >
            <Link aria-label="Go to favorites items" href="/admin" prefetch={false}>
                <UserHomeIcon />
            </Link>
            {isPopupOpen && userInfo &&
                <div
                    className="fixed z-50 bg-white border rounded-md shadow-md py-4 w-[160px] h-auto flex flex-col gap-4"
                >
                    <div><Link href="/admin/info" className="w-full p-3 m-3 rounded"> 我的主页 </Link></div>
                    <div><Link href="/admin/product" className="w-full  p-3 m-3 rounded"> 我的商品 </Link></div>
                    {userInfo.role == "admin" && <div><Link href="/admin/flashSale" className="w-full p-3 m-3 rounded"> 秒杀系统管理 </Link></div>}
                    <div><Link href="/admin/orders" className="w-full  p-3 m-3 rounded"> 订单 </Link></div>
                    <div><Link href="/login" className="w-full p-3 m-3 rounded" onClick={onLogout}> 登出 </Link></div>
                </div>}
        </div>
    )
}
