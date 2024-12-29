"use client"

import { cn } from "@/utils/cn"
import { BagIcon } from "../icons/bag-icon"
import { OpenCartButton } from "./open-cart-button"
import { useCartStore } from "@/stores/cart-store"

interface CartProps {
  className?: string
}

export function Cart({ className }: CartProps) {
  const cart = useCartStore((s) => s.cart)
  const preloadSheet = useCartStore((s) => s.preloadSheet)

  return (
    <div className={cn("relative size-8 cursor-pointer items-center justify-center fill-none transition-transform hover:scale-105", className)} onMouseOver={preloadSheet}>
      <BagIcon className="text-black" />
        <div className="absolute bottom-0 right-0 flex size-4 items-center justify-center rounded-full bg-black text-[11px] text-white">{cart?.orders.length}</div>
      <OpenCartButton />
    </div>
  )
}
