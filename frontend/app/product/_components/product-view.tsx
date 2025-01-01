'use client'
import { BackButton } from "@/components/back-button";
import { notFound } from "next/navigation";
import { useState, useEffect } from "react";
import { Product } from "@/types";
import { AddToCartButton } from "./add-to-cart-button";
import { ProductTitle } from "./product-title";
import { RightSection } from "./right-section";
import Image from 'next/image';

export default function ProductView({ productId }: { productId: string }) {
    const [product, setProduct] = useState<Product | null>(null)
    useEffect(() => {
        fetch(`/api/v1/product/${productId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((res) => res.json())
            .then((data) => {
                setProduct(data)
            })
            .catch((err) => {
                console.log(err)
                return notFound()
            })
    }, [])

    return product && (
        <div className="relative mx-auto max-w-container-md min-h-[80vh] min-w-[80vw] px-4 xl:px-0">
            <div className="mb:pb-8 relative flex w-full items-center justify-center gap-10 py-4 md:pt-12">
                <BackButton className="left-2 mb-8 hidden md:block xl:absolute" />
                <div className="mx-auto w-full max-w-container-sm">
                </div>
            </div>
            <main className="mx-auto max-w-container-sm">
                <Image priority={true} src={product.img || "/hero.png"} alt={product.name} sizes="400px" width={100} height={100} />
                <div className="grid grid-cols-1 gap-4 md:mx-auto md:max-w-screen-xl md:grid-cols-12 md:gap-8">
                    <ProductTitle
                        className="md:hidden"
                        title={product.name}
                        price={product.price.toString()}
                    />
                    <RightSection className="md:col-span-6 md:col-start-8 md:mt-0">
                        <ProductTitle
                            className="hidden md:col-span-4 md:col-start-9 md:block"
                            title={product.name}
                            price={product.price.toString()}
                        />
                        <p>{product.description}</p>
                        <AddToCartButton className="mt-4" product={product} />
                    </RightSection>
                </div>
            </main>
        </div>
    )
}