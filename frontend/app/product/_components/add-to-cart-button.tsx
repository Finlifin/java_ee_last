"use client"

import { useState } from "react"
import { toast } from "sonner"
import { BagIcon } from "@/components/icons/bag-icon"
import { Button } from "@/components/ui/button"
import { cn } from "@/utils/cn"
import { useCartStore } from "@/stores/cart-store"
import type { Product } from "@/types"

export function AddToCartButton({ className, product }: { className?: string; product: Product }) {
  const [isPending, setIsPending] = useState(false)
  const { refresh } = useCartStore((s) => s)

  const handleClick = async () => {
    setIsPending(true)
    const session = localStorage.getItem("session")
    if (!session) {
      toast.error("Please login to add items to cart")
      return
    }
    fetch(`/api/v1/cart/add?productId=${product.id}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${JSON.parse(session).token}`,
        },
      })
      .then(() => {
        toast.success("Product added to cart")
        refresh()
        setIsPending(false)
      })
      .catch((err) => {
        console.log(err)
      })
  }
  return (
    <Button
      onClick={handleClick}
      disabled={isPending}
      variant="default"
      className={cn("mx-auto w-full rounded-md p-10 py-4 transition-all hover:scale-105 md:w-full md:rounded-md md:py-4", className)}
    >
      <BagIcon className="mr-2 size-5 text-white" />
      Add to Bag
    </Button>
  )
}
