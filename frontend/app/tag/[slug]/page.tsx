import { SearchView } from "@/components/search-view"

export default async function Page({
  params,
}: {
  params: Promise<{ slug: string }>
}) {
  const slug = decodeURIComponent((await params).slug)
  return <div>
    <SearchView tag={slug} />
  </div>
}