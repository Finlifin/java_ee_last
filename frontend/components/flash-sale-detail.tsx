'use client'
import { useEffect, useState } from "react";
import { defaultUserInfo, FlashSale, Product, UserInfo } from "@/types/index";
import { FlashSaleCard } from "./flash-sale-card";

export default function FlashSaleDetail({ flashSaleId }: { flashSaleId: string }) {
  const [flashSale, setFlashSale] = useState<FlashSale | null>(null);
  const [product, setProduct] = useState<Product | null>(null);
  const [seller, setSeller] = useState<UserInfo>(defaultUserInfo);

  useEffect(() => {
    async function fetchData() {
      let response = await fetch(`/api/v1/flashsale/${flashSaleId}`);
      if (response.ok) {
        const flashSaleData = await response.json();
        setFlashSale(flashSaleData);

        response = await fetch(`/api/v1/product/${flashSaleData.productId}`);
        if (response.ok) {
          const productData = await response.json();
          setProduct(productData);

          response = await fetch(`/api/v1/user/info/${productData.seller}`);
          if (response.ok) {
            setSeller(await response.json());
          } else {
            console.log(`Failed to fetch seller data for product ${flashSaleData.productId}`);
          }
        } else {
          console.log(`Failed to fetch product data for flash sale ${flashSaleId}`);
        }
      } else {
        console.log(`Failed to fetch flash sale data for flash sale ${flashSaleId}`);
      }
    }

    fetchData();
  }, [flashSaleId]);

  return (
    <div className="flash-sale-detail">
        {flashSale && product && (
            <div>
            <h1>{product.name}</h1>
            <h2>{flashSale.discount * 100}% OFF</h2>
            <p>{flashSale.description}</p>
            <p>原价: ¥{product.price}</p>
            <p>到手价: ¥{(product.price * (1 - flashSale.discount)).toFixed(2)}</p>
            <p>商家：{seller.username}</p>
            </div>
        )}
    </div>
  );
}
