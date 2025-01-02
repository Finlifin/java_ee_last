import Image from "next/image"
import Link from "next/link"
import type {  Product, UserInfo } from "@/types"

export const ProductCard = ({ product: { img, name, description, id, price }, seller }: { product: Product, seller: UserInfo }) => {
  const href = `/product/${id}`

  return (
    <Link className={"group flex h-full w-full flex-col overflow-hidden"} href={href}>
      <div className="relative aspect-square overflow-hidden m-auto">
        <Image
          className="object-cover transition-transform group-hover:scale-105"
          src={'/' + img || "/hero.png"}
          alt={name}
          fill
        />
      </div>
      <div className="flex shrink-0 grow flex-col py-4">
        <h3 className="line-clamp-2 text-lg font-semibold transition-colors">{name}</h3>
        <div className="flex flex-col gap-1">
          <span>{price}¥</span>

          {!!seller.username && <p className="text-sm text-gray-500">{seller.username}</p>}
          <div className="mt-1 flex flex-wrap items-center gap-1">
          </div>
        </div>
      </div>
    </Link>
  )
}
