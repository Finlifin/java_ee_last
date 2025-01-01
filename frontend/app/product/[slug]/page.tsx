import ProductView from "../_components/product-view"

export default async function ProductPage({ params }: { params: Promise<{ slug: string }> }) {
  const { slug } = await params;

  return <ProductView productId={slug} />
}