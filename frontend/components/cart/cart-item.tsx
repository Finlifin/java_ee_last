/* eslint-disable react/no-children-prop */

import Image from "next/image"
import Link from "next/link"
import { PlatformCartItem } from "@/lib/shopify/types"
import { cn } from "@/utils/cn"
import { ChangeQuantityButton } from "./change-quantity-button"
import { DeleteButton } from "./delete-button"
import { Order, Product } from "@/types"


export function CartItem({ order, product }: { order: Order, product: Product }) {
  return (
    <li className="flex items-center justify-between gap-6 py-2">
      <div className="flex h-[115px] w-[90px] shrink-0 items-center bg-neutral-100">
        <Image
          src={product.img || "/default-product-image.svg"}
          alt={product.name || ""}
          width={115}
          height={90}
          sizes="100px"
        />
      </div>
      <div className="flex flex-1 flex-col items-start justify-around gap-0.5 text-[13px]">
        <Link href={`/product/${order.productId}`}>
          <h2 className="line-clamp-1 hover:underline">{product.name}</h2>
          {/* <p className="line-clamp-1 text-neutral-500">{props.merchandise.title || ""}</p> */}
        </Link>
        <p className="py-2 font-bold">{order.totalAmount + " ¥"}</p>
        <div className="flex w-full items-center justify-between">
          <DeleteButton id={order.productId} />

          <div className="boder-black flex h-[32px] w-[100px] justify-between border p-4 text-[14px] text-neutral-500">
            <ChangeQuantityButton id={order.productId} quantity={order.quantity - 1} productId={order.productId} children={"-"} />
            <div className="flex cursor-not-allowed items-center gap-2 text-black">{order.quantity}</div>
            <ChangeQuantityButton id={order.productId} quantity={order.quantity + 1} productId={order.productId} children={"+"} />
          </div>
        </div>
      </div>
    </li>
  )
}
