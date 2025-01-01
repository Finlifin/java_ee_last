import { PlatformProduct } from "@/lib/shopify/types"

export type SearchParamsType = Record<string, string | string[] | undefined>

export type CommerceProduct = PlatformProduct & {
  // These are opt-in features, must exist in meilisearch index first (They are set via cron jobs)
  avgRating?: number
  totalReviews?: number
  reviewsSummary?: string
  hierarchicalCategories?: {
    lvl0?: string[]
    lvl1?: string[]
    lvl2?: string[]
  }
}

export type Product = {
  id: number,
  name: string,
  description: string
  tags: string[],
  price: number,
  img: string,
  seller: number,
  stock: number,
  state: 'active' | 'inactive',
}

export type UserInfo = {
  id: number
  username: string
  email: string
  avatar: string
  role: 'admin' | 'user'
}

export const defaultUserInfo: UserInfo = {
  id: 1,
  username: 'fin',
  email: 'fin@valar.arda',
  avatar: '/hero.png',
  role: 'admin',
}

export type Order = {
  id: number,
  customerId: number,
  productId: number,
  quantity: number,
  totalAmount: number,
  creationDateTime: string,
  updateDateTime: string,
  state: 'active' | 'pending' | 'cancelled' | 'refunded' | 'delivered' | 'overdue' | 'payed' | 'returned',
}

export type Cart = {
  products: Product[],
  orders: Order[],
}

export function newCart(products: Product[], orders: Order[]): Cart {
  return { products, orders }
}

export type FlashSale = {
  id: number,
  productId: number,
  description: string,
  creatorId: number,
  discount: number,
  limitPerUser: number,
  totalQuantity: number,
  startTime: string,
  endTime: string,
  creationDateTime: string,
  updateDateTime: string,
  state: 'created' | 'active' | 'cancelled' | 'ended',
}