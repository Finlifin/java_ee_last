import { useTransition } from "react"
import { toast } from "sonner"

import { updateItemQuantity } from "@/app/actions/cart.actions"
import { Spinner } from "@/components/spinner"
import { useCartStore } from "@/stores/cart-store"

interface ChangeQuantityButtonProps {
  id: number
  quantity: number
  productId: number
  children: React.ReactNode
}

export function ChangeQuantityButton({ id, quantity, productId, children }: ChangeQuantityButtonProps) {
  const { refresh } = useCartStore()
  const [isPending, startTransition] = useTransition()

  const handleClick = () => {
    let session = localStorage.getItem('session');

    startTransition(async () => {
      fetch(`/api/v1/cart/setQuantity?productId=${productId}&quantity=${quantity}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${JSON.parse(session as string).token}`,
          },
        }
      ).then(() => {
        refresh()
      }).catch(err => {
        console.log(err)
      })
    })
  }

  return (
    <div className="relative flex h-full w-fit items-center">
      <button className="flex cursor-pointer items-center gap-2 bg-transparent transition-transform hover:scale-150" onClick={handleClick} disabled={isPending}>
        {isPending ? <Spinner className="size-2" /> : children}
      </button>
    </div>
  )
}
