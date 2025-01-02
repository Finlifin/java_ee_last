'use client'
import { useUserInfoStore } from '@/stores/user-info-store'
import { Order, Product } from '@/types'
import Link from 'next/link'
import { useEffect, useState } from 'react'

function OrderView() {
  let [orders, setOrders] = useState<Order[] | null>(null)
  let [products, setProducts] = useState<Map<number, Product> | null>(null)
  let { userInfo } = useUserInfoStore()

  useEffect(() => {
    fetchData(userInfo?.id || 1).then(({ orders, products }) => {
      setOrders(orders)
      setProducts(products)
    })
  }, [])


  return (
    <div
      className='flex flex-col items-center justify-center m-10'
    >
      <h1 className='text-4xl'>订单</h1>
      {orders === null && <div>loading...</div>}
      {orders && orders?.length == 0 && <div>暂无订单</div>}
      {orders && orders?.length != 0 && <div>
        {orders?.map((order) => (
          <OrderItem
            key={order.id}
            order={order}
            product={products?.get(order.productId) || { id: order.productId, name: 'loading' } as Product}
          />
        ))}
      </div>}
    </div>
  )
}

function OrderItem({ order, product }: { order: Order, product: Product }) {

  return <div className='border border-gray-300 rounded p-4 m-4 w-min-[300px]'>
    <div> 订单号: {order.id}</div>
    <div><Link href={`/product/${order.productId}`}>商品: {product.name}</Link></div>
    <div>价格: {order.totalAmount}</div>
    <div>下单日期: {order.creationDateTime}</div>
    <div>状态: {order.state == 'payed' ? '已支付' :
      order.state == 'pending' ? '待支付' :
        order.state == 'cancelled' ? '已取消' :
          order.state == 'overdue' ? '已逾期' :
            order.state == 'delivered' ? '已派发' :
              order.state == 'refunded' ? '已退款' :
                order.state == 'returned' ? '已退货' :
                  '未知状态'
    }
      {order.state == 'pending' && <Link className='rounded bg-red-300' href={`/order/${order.id}`}>完成支付</Link>}
    </div>
  </div>
}

async function fetchData(userId: number) {
  let orders = await fetch(`/api/v1/order/of/${userId}`).then(res => res.json())
  let products = new Map<number, Product>()
  orders.forEach(async (order: Order) => {
    let product = await fetch(`/api/v1/product/${order.productId}`).then(res => res.json())
    if (product) products.set(order.productId, product)
  })

  return { orders, products }
}

export default OrderView