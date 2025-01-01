'use client'
import Link from "next/link";
import { useEffect, useState } from "react";

export default function Home() {
  let [tags, setTags] = useState<string[]>([]);

  useEffect(() => {
    async function fetchData() {
      let response = await fetch("/api/v1/product/tags");
      if (response.ok) {
        setTags(await response.json());
      } else {
        console.log("Failed to fetch tags");
      }
    }

    fetchData();
  }, [])

  return <>
    <Link href="/flashSales">秒杀活动</Link>
    {tags.map(tag => <Link href={`/tag/${encodeURIComponent(tag)}`} key={tag}>{tag}</Link>)}
  </>;
}
