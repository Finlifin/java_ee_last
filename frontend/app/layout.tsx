import "./globals.css"

import Script from "next/script"
// import { Suspense } from "react"
// import { Toaster } from "sonner"

import { Metadata } from "next"
// import { CartView } from "@/components/cart/cart-view"
// import { DemoModeAlert } from "@/components/demo-mode-alert"
// import DraftToolbar from "@/components/draft-toolbar"
import { Footer } from "@/components/footer"
// import { GithubBadge } from "@/components/github-badge"
// import { Modals } from "@/components/modals/modals"
// import { mobileInlineScript } from "@/components/navigation-bar/mobile-inline-script"
import { NavigationBar } from "@/components/navigation-bar/navigation-bar"
import { NavItem } from "@/components/navigation-bar/types"
import { Toaster } from "sonner"
import { CartView } from "@/components/cart/cart-view"
import { Modals } from "@/components/modals/modals"
// import { ThirdParties } from "@/components/third-parties"
// import { FlagValues } from "@vercel/flags/react"
// import { env } from "process"


export const revalidate = 86400

const navigationItems: NavItem[] = [
  {
    text: "时尚新品",
    href: "/tag/时尚新品",
    submenu: {
      variant: "text-grid",
      items: [
        {
          text: "女性",
          href: "/tag/女性",
          items: [
            { text: "衬衫和衬衣", href: "/tag/衬衫和衬衣" },
            { text: "西装和背心", href: "/tag/西装和背心" },
            { text: "开衫和毛衣", href: "/tag/开衫和毛衣" },
            { text: "连衣裙", href: "/tag/连衣裙" },
            { text: "裙子", href: "/tag/裙子" },
          ],
        },
        {
          text: "男性",
          href: "/tag/男性",
          items: [
            { text: "T恤和背心", href: "/tag/T恤和背心" },
            { text: "连帽衫和卫衣", href: "/tag/连帽衫和卫衣" },
            { text: "西装和套装", href: "/tag/西装和套装" },
            { text: "短裤", href: "/tag/短裤" },
            { text: "外套", href: "/tag/外套" },
          ],
        },
        {
          text: "儿童",
          href: "/tag/儿童",
          items: [
            { text: "服装", href: "/tag/服装" },
            { text: "运动服", href: "/tag/运动服" },
            { text: "配饰", href: "/tag/配饰" },
            { text: "鞋类", href: "/tag/鞋类" },
          ],
        },
      ],
    },
  },
  {
    text: "电子产品",
    href: "/tag/电子产品",
    submenu: {
      variant: "text-grid",
      items: [
        {
          text: "音频设备",
          href: "/tag/音频设备",
          items: [
            { text: "耳机", href: "/tag/耳机" },
            { text: "扬声器", href: "/tag/扬声器" },
          ],
        },
        {
          text: "相机",
          href: "/tag/相机",
          items: [
            { text: "数码相机", href: "/tag/数码相机" },
            { text: "运动相机", href: "/tag/运动相机" },
          ],
        },
        {
          text: "智能手机",
          href: "/tag/智能手机",
        },
        {
          text: "笔记本电脑",
          href: "/tag/笔记本电脑",
        },
        {
          text: "显示器",
          href: "/tag/显示器",
        },
      ],
    },
  },
  {
    text: "运动与户外",
    href: "/tag/运动与户外",
    submenu: {
      variant: "text-grid",
      items: [
        {
          href: "/tag/健身器材",
          text: "健身器材",
        },
        {
          href: "/tag/户外装备",
          text: "户外装备",
        },
        {
          href: "/tag/运动服",
          text: "运动服",
        },
        {
          href: "/tag/运动鞋",
          text: "运动鞋",
        },
      ],
    },
  },
  {
    text: "美容",
    href: "/tag/美容",
    submenu: {
      variant: "text-grid",
      items: [
        {
          text: "护肤",
          href: "/tag/护肤",
          items: [
            { text: "清洁产品", href: "/tag/清洁产品" },
            { text: "保湿产品", href: "/tag/保湿产品" },
            { text: "治疗和精华", href: "/tag/治疗和精华" },
          ],
        },
        {
          text: "化妆",
          href: "/tag/化妆",
          items: [
            { text: "面部化妆", href: "/tag/面部化妆" },
            { text: "眼部化妆", href: "/tag/眼部化妆" },
            { text: "唇部化妆", href: "/tag/唇部化妆" },
          ],
        },
        {
          text: "护发",
          href: "/tag/护发",
          items: [
            { text: "洗发水和护发素", href: "/tag/洗发水和护发素" },
            { text: "造型产品", href: "/tag/造型产品" },
          ],
        },
        {
          text: "香水",
          href: "/tag/香水",
          items: [
            { text: "香水", href: "/tag/香水" },
            { text: "身体喷雾", href: "/tag/身体喷雾" },
          ],
        },
      ],
    },
  },
  {
    text: "家具",
    href: "/tag/家具",
    submenu: {
      variant: "text-grid",
      items: [
        {
          text: "客厅",
          href: "/tag/客厅",
          items: [
            { text: "沙发和组合沙发", href: "/tag/沙发和组合沙发" },
            { text: "咖啡桌", href: "/tag/咖啡桌" },
            { text: "电视柜", href: "/tag/电视柜" },
          ],
        },
        {
          text: "卧室",
          href: "/tag/卧室",
          items: [
            { text: "床", href: "/tag/床" },
            { text: "梳妆台", href: "/tag/梳妆台" },
            { text: "床头柜", href: "/tag/床头柜" },
          ],
        },
        {
          text: "办公室",
          href: "/tag/办公室",
          items: [
            { text: "书桌", href: "/tag/书桌" },
            { text: "办公椅", href: "/tag/办公椅" },
            { text: "储物解决方案", href: "/tag/储物解决方案" },
          ],
        },
      ],
    },
  },
]

export const metadata: Metadata = {
  title: "Next.js Enterprise Commerce | Blazity",
  description: "AI-FIRST NEXT.JS STOREFRONT FOR COMPOSABLE COMMERCE",
}

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <NavigationBar items={navigationItems} />

        {children}

        <Footer />
        <Modals />

        <CartView />

        <Toaster position="bottom-left" />
      </body>
    </html>
  )
}
