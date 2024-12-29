"use client"

import { useEffect, useState, useTransition } from "react"
import dynamic from "next/dynamic"
import { useCartStore } from "@/stores/cart-store"
import { Order, Product, Cart, newCart } from "@/types"

const CartSheet = dynamic(() => import("@/components/cart/cart-sheet").then((mod) => mod.CartSheet))

export function CartView() {
  const [isPending, startTransition] = useTransition()
  const [logined, setLogined] = useState(false)
  const { isOpen, isSheetLoaded, openCart, closeCart, setCart, cart } = useCartStore()
  const { lastUpdatedAt } = useCartStore()

  const getCart = () => {
    let session = localStorage.getItem("session")
    if (!session) {
      window.location.href = "/login"
    }
    let products: Product[] = []
    let orders: Order[] = []
    fetch("/api/v1/cart",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${JSON.parse(session || "").token}` as string,
        },
      }
    )
      .then((res) => res.json())
      .then((data) => {
        console.log("get cart: ", data)
        orders = data.orders
      })
      .catch((err) => {
        console.error(err)
      })

    products = orders.map((order: Order) => {
      let product: Product = {} as Product
      fetch(`/api/v1/product/${order.productId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        },
      ).then(res => res.json())
        .then(data => {
          console.log(`get product ${order.productId}: `, data)
          product = data
        }).catch(err => {
          console.error(err)
        })
      return product
    })

    return newCart(products, orders)
  }

  useEffect(() => {
    const session = localStorage.getItem("session")
    if (session) {
      setLogined(true)
    }
    startTransition(async () => {
      cart && setCart(getCart() || null)
    })
  }, [])

  return isSheetLoaded && <CartSheet isPending={isPending} isOpen={isOpen} onCartOpen={openCart} cart={cart} onCartClose={closeCart} />
}