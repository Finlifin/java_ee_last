import { Product } from "@/types/index"
import { UserInfo, defaultUserInfo } from "@/types"
import { ProductCard } from "./product-card"

export function ProductView({ products, sellers }: { products: Product[], sellers: Map<number, UserInfo> }) {
  return <div className="flex gap-12 md:gap-24">
    <div className="w-full">
      <div className="px-4"></div>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {products.map((product: Product) => {
          return (
            <ProductCard key={product.id} product={product} seller={sellers.get(product.id) || defaultUserInfo} />
          )
        })}
        {/* <HitsSection hits={hits} /> */}
        {/* <PaginationSection queryParams={searchParams} totalPages={totalPages} /> */}
      </div>
    </div>
  </div>
}
