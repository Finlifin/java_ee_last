import { useEffect, useState } from "react";
import { defaultUserInfo, FlashSale, Product, UserInfo } from "@/types/index";
import Link from "next/link";

export const FlashSaleCard = ({ flashSale }: { flashSale: FlashSale }) => {
    const [product, setProduct] = useState<Product | null>(null);
    const [seller, setSeller] = useState<UserInfo>(defaultUserInfo);

    useEffect(() => {
        async function fetchData() {
            let response = await fetch(`/api/v1/product/${flashSale.productId}`);
            if (response.ok) {
                const productData = await response.json();
                setProduct(productData);

                response = await fetch(`/api/v1/user/info/${productData.seller}`);
                if (response.ok) {
                    setSeller(await response.json());
                } else {
                    console.log(`Failed to fetch seller data for product ${flashSale.productId}`);
                }
            } else {
                console.log(`Failed to fetch product data for flash sale ${flashSale.id}`);
            }
        }

        fetchData();
    }, []);

    if (!product) {
        return <div>Loading...</div>;
    }

    return (
        <Link href={`/flashSale/${flashSale.id}`} className="card">
            <img src={product.img || "/hero.png"} alt={flashSale.description} />
            <div className="card-body">
                <h2 className="card-text"
                    style={{ color: "red", fontWeight: "bold", fontSize: "2.5rem" }}
                >{flashSale.discount * 100}% OFF</h2>
                <h5 className="card-title">{product.name}</h5>
                <p className="card-text">{flashSale.description}</p>
                {/* <p className="card-text">开始时间: {flashSale.startTime}</p>
                <p className="card-text">结束时间: {flashSale.endTime}</p> */}
                <p className="card-text">原价: ¥{product.price}</p>
                <p className="card-text">到手价: ¥{(product.price * (1 - flashSale.discount)).toFixed(2)}</p>
                <p className="card-text">商家：{seller.username}</p>
            </div>
        </Link>
    );
};
