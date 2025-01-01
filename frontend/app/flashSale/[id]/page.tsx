import FlashSaleDetail from "@/components/flash-sale-detail";

export default async function FlashSalePage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;

  return <FlashSaleDetail flashSaleId={id} />
}
