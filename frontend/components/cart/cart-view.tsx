"use client"

import { useEffect, useTransition } from "react"
import dynamic from "next/dynamic"
import { getCart, useCartStore } from "@/stores/cart-store"
import { useUserInfoStore } from "@/stores/user-info-store"

const CartSheet = dynamic(() => import("@/components/cart/cart-sheet").then((mod) => mod.CartSheet))

export function CartView() {
  const [isPending, startTransition] = useTransition()
  const { isOpen, openCart, closeCart, setCart, cart } = useCartStore()
  const { setUserInfo } = useUserInfoStore()

  useEffect(() => {
    startTransition(async () => {
      getCart().then((cart) => {
        setCart(cart)
      })

      const session = localStorage.getItem("session")
      if (session) {
        const userInfo = JSON.parse(session).userInfo
        setUserInfo(userInfo)
      }
    })

  }, [])

  return <CartSheet isPending={isPending} isOpen={isOpen} onCartOpen={openCart} onCartClose={closeCart} />
}