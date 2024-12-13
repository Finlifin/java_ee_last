"use client"

import { useModalStore } from "@/stores/modal-store"
import { Button } from "../ui/button-old"
import { ThinSearchIcon } from "../icons/thin-search-icon"

export function SearchButton() {
  const { openModal } = useModalStore()

  return (
    <Button aria-label="Launch search modal" variant="ghost" className="ml-3 flex items-center justify-center p-1" onClick={() => openModal("search")}>
      <ThinSearchIcon className="size-6" />
    </Button>
  )
}
