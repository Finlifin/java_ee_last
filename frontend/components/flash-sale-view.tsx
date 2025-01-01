'use client'

import { defaultUserInfo, FlashSale, Product } from "@/types/index"
import { UserInfo } from "@/types"
import { FlashSaleCard } from "./flash-sale-card"
import { useEffect, useState } from "react";

export default function FlashSaleView() {
    let [flashSales, setFlashSales] = useState<FlashSale[]>([]);

    useEffect(() => {
        fetchFlashSales().then((flashSales: FlashSale[]) => {
            setFlashSales(flashSales);
        });
    }, [])

    return <>
        <h1 className="text-3xl font-bold text-center m-5">秒杀活动</h1>
        <div className="flex gap-12 md:gap-24 m-5">
            <div className="w-full">
                <div className="px-4"></div>
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                    {flashSales.map((flashSale: FlashSale) => {
                        return (
                            <FlashSaleCard
                                key={flashSale.id}
                                flashSale={flashSale}
                            />
                        )
                    })}
                </div>
            </div>
        </div></>
}


async function fetchFlashSales(): Promise<FlashSale[]> {
    // Implement the logic to fetch flash sales data
    let response = await fetch("/api/v1/flashsale");
    if (response.ok) {
        return await response.json();
    }
    if (response.status === 401) {
        window.location.href = "/login";
    }

    console.log("Failed to fetch flash sales data");
    return [];
}

async function fetchExtra(flashSales: FlashSale[]): Promise<[Map<number, UserInfo>, Map<number, Product>]> {
    let sellers = new Map<number, UserInfo>();
    let products = new Map<number, Product>();

    flashSales.forEach(async (flashSale) => {
        let response = await fetch(`/api/v1/product/${flashSale.productId}`);
        if (response.ok) {
            products.set(flashSale.productId, await response.json());
        } else {
            console.log(`Failed to fetch product data for flash sale ${flashSale.id}`);
        }

        response = await fetch(`/api/v1/user/info/${(products.get(flashSale.productId) as Product).seller}`);
        if (response.ok) {
            sellers.set(flashSale.productId, await response.json());
        }
        else {
            console.log(`Failed to fetch seller data for product ${flashSale.productId}`);
        }
    })

    return [sellers, products];
}