'use client'
import { useEffect, useState } from "react"
import { defaultUserInfo, UserInfo } from "@/types"
import { Product } from "@/types/index"
import { ProductCard } from "./product-card"
import { ProductView } from "./product-view"

export function SearchView({ tag }: { tag: string }) {
  const [products, setProducts] = useState<Product[]>([])
  const [productFilters, setProductFilters] = useState<Product | null>(null)
  const [sellers, setSellers] = useState<Map<number, UserInfo>>(new Map())

  useEffect(() => {
    // get products
    fetch(`/api/v1/product/tag?tag=${tag}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      }
    ).then(res => res.json())
      .then(data => {
        console.log(`get products of tag ${tag}: `, data)
        setProducts(data)
        let sellers_ = new Map<number, UserInfo>()

        data.map((product: Product) => {
          // get seller info
          fetch(`/api/v1/user/info/${product.seller}`,
            {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json'
              }
            }
          ).then(res => res.json())
            .then(data => {
              console.log(`get seller info of product ${product.id}: `, data)
              sellers_.set(product.id, data)
            }).catch(err => {
              console.error(err)
            })
        })

        setSellers(sellers_)
      }).catch(err => {
        console.error(err)
      })
  }, [])
  return (
    <div className="mx-auto w-full max-w-[1920px] p-4 md:px-12 md:pb-24 md:pt-4">
      <div className="sticky top-[77px] z-40 flex items-center justify-between bg-white py-4 md:top-[83px] md:-mx-12 md:px-12">
        <h1 className="flex items-center gap-1 text-3xl font-semibold tracking-tight lg:text-1xl">
          <span className="hidden lg:block">共找到{products.length}个结果</span>
        </h1>
      </div>
      <ProductView products={products} sellers={sellers} />
    </div>
  )
}