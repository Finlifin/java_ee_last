import { Cart, newCart, Order, Product } from "@/types"
import { create } from "zustand"

interface CartStore {
  isOpen: boolean
  isSheetLoaded: boolean
  lastUpdatedAt: number
  cart: Cart | null
  checkoutReady: boolean

  openCart: () => void
  closeCart: () => void
  preloadSheet: () => void
  refresh: () => void
  setCart: (payload: Cart | null) => void
  setCheckoutReady: (payload: boolean) => void
}

export const useCartStore = create<CartStore>((set) => ({
  isOpen: false,
  lastUpdatedAt: 0,
  cart: null,
  isSheetLoaded: false,
  checkoutReady: true,

  openCart: () => set(() => ({ isOpen: true, isSheetLoaded: true, lastUpdatedAt: Date.now() })),
  closeCart: () => set(() => ({ isOpen: false, isSheetLoaded: true, lastUpdatedAt: Date.now() })),
  preloadSheet: () => set(() => ({ isSheetLoaded: true })),
  refresh: () => set(() => {
    getCart().then((cart) => set({ cart }))
    return {
      lastUpdatedAt: Date.now()
    }
  }),
  setCheckoutReady: (payload: boolean) => set(() => ({ checkoutReady: payload })),
  setCart: (payload: Cart | null) => set(() => ({ cart: payload })),
}))

export const getCart = async () => {
  let session = localStorage.getItem("session")
  if (!session) {
    return newCart([], [])
  }
  let products: Product[] = []
  let orders: Order[] = []
  try {
    const cartResponse = await fetch("/api/v1/cart", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${JSON.parse(session || "").token}` as string,
      },
    })
    const cartData = await cartResponse.json()
    orders = cartData.orders

    for (const order of orders) {
      const productResponse = await fetch(`/api/v1/product/${order.productId}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })
      const productData = await productResponse.json()
      products.push(productData)
    }
  } catch (err) {
    console.log(err)
  }

  let result = newCart(products, orders)
  console.log(result)

  return result
}