import { useTransition } from "react"

import { removeCartItem } from "@/app/actions/cart.actions"
import { LoadingDots } from "@/components/loading-dots"
import { useCartStore } from "@/stores/cart-store"
import { cn } from "@/utils/cn"

interface DeleteButtonProps {
  id: number
}

export function DeleteButton({ id }: DeleteButtonProps) {
  const refresh = useCartStore((prev) => prev.refresh)
  const [isPending, startTransition] = useTransition()

  const handleClick = () => {
    let session = localStorage.getItem('session');
    if (!session) {
      window.location.href = '/login'
    }

    startTransition(async () => {
      fetch(`/api/v1/cart/remove?productId=${id}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${JSON.parse(session as string).token}`,
          },
        }
      ).then(res => res.json())
        .then(data => {
          console.log(`remove product ${id}: `, data)
          refresh()
        }).catch(err => {
          console.error(err)
        })
    })
  }

  return (
    <div className={cn("flex w-fit gap-2", { "pointer-events-none": isPending })}>
      <button className="bg-transparent text-[13px] text-neutral-500 underline hover:no-underline" onClick={handleClick} disabled={isPending}>
        Delete
      </button>
      {isPending && <LoadingDots />}
    </div>
  )
}
