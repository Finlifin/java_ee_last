'use client'
import { ProductCard } from "@/components/product-card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useUserInfoStore } from "@/stores/user-info-store"
import { FlashSaleApplication, Product, UserInfo } from "@/types"
import { useEffect, useState } from "react"
import Image from 'next/image'
import Link from "next/link"

function MyProducts() {
  let [products, setProducts] = useState<Product[] | null>(null)
  let [applications, setApplications] = useState<FlashSaleApplication[] | null>(null)
  let { userInfo } = useUserInfoStore()
  let [isPopupOpen, setIsPopupOpen] = useState(false)
  let [isSucceed, setIsSucceed] = useState(false)

  useEffect(() => {
    let session = localStorage.getItem('session')
    let userId = JSON.parse(session || "").userInfo.id

    fetchData(userId).then((products) => {
      setProducts(products)
    })

    fetchMyApplications(userId).then((applications) => {
      setApplications(applications)
    })
  }, [isSucceed])

  return (
    <div className="flex flex-col items-right justify-center m-10 gap-5">
      <h1 className="text-4xl">我的商品</h1>
      {products === null && <div>loading...</div>}
      {products && products?.length == 0 && <div>暂无商品</div>}
      {products && products?.length != 0 && <div>
        {products?.map((product) => (
          <ProductItem key={product.id} product={product} />
        ))}
      </div>}
      {isPopupOpen && <CreateProductForm close={() => setIsPopupOpen(false)} succeed={() => setIsSucceed(!isSucceed)} />}
      <h1 className="text-4xl">秒杀申请</h1>
      {applications === null && <div>loading...</div>}
      {applications && applications?.length == 0 && <div>暂无秒杀申请</div>}
      {applications && applications?.length != 0 && <div>
        {applications?.map((application) => (
          <FlashSaleApplicationItem key={application.id} application={application} />
        ))}
      </div>}
      <Button
        style={{
          maxWidth: 'fit-content'
        }}
        onClick={() => setIsPopupOpen(true)}
      >发布新品</Button>
    </div>
  )
}

function ProductItem({ product: { img, name, description, id, price, tags } }: { product: Product }) {
  const href = `/product/${id}`
  const [isFlashSalePopupOpen, setIsFlashSalePopupOpen] = useState(false)

  return (
    <div className={"group flex h-full w-fit flex-col overflow-hidden border p-3 m-3 rounded shadow"}>
      <div className="relative aspect-square overflow-hidden m-auto">
        <Image
          className="object-cover transition-transform group-hover:scale-105"
          src={"/hero.png"}
          alt={name}
          fill
        />
      </div>
      <div className="flex shrink-0 grow flex-col py-4">
        <Link className="line-clamp-2 text-lg font-semibold transition-colors" href={href}>{name}</Link>
        <div className="flex flex-col gap-1">
          <span className="text-red-500">{price}¥</span>
          <span className=" opacity-75">{tags.join(',')}</span>
          <span>{description}</span>
        </div>
      </div>
      <div className="flex gap-2">
        <Button>下架</Button>
        <Button>修改信息</Button>
        <Button onClick={() => setIsFlashSalePopupOpen(true)}>申请开启秒杀活动</Button>
        {isFlashSalePopupOpen && <FlashSaleApplicationForm close={() => setIsFlashSalePopupOpen(false)} productId={id} />}
      </div>
    </div>
  )
}

function CreateProductForm({ close, succeed }: { close: () => void, succeed: () => void }) {
  let [name, setName] = useState('')
  let [price, setPrice] = useState('')
  let [description, setDescription] = useState('')
  let [image, setImage] = useState('')
  let [stock, setStock] = useState('')
  let [tags, setTags] = useState('')
  const { userInfo } = useUserInfoStore()

  const submit = () => {
    fetch('/api/v1/product/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${JSON.parse(localStorage.getItem('session') || "").token}`
      },
      body: JSON.stringify({
        name,
        price: parseFloat(price),
        description,
        img: image.split('\\').at(-1),
        stock: parseInt(stock),
        tags: tags.split(' '),
        seller: userInfo?.id
      })
    }).then((res) => res.json())
      .then((data) => {
        console.log(data)
        succeed()
        close()
      })
  }

  return (
    <div
      style={{ backdropFilter: 'blur(8px)' }}
      onClick={e => { e.stopPropagation(); close() }}
      className="flex flex-col items-center justify-center fixed z-50 bg-[#000000a0] w-full h-full top-0 left-0">
      <div
        onClick={e => e.stopPropagation()}
        className="flex flex-col items-center justify-center m-10 gap-4 bg-white p-10 rounded-lg"
      >
        <Input type="text" placeholder="商品名称" defaultValue={name} onChange={e => setName(e.target.value)} />
        <Input type="number" placeholder="商品价格" defaultValue={price} onChange={e => setPrice(e.target.value)} />
        <Input type="text" placeholder="商品描述" defaultValue={description} onChange={e => setDescription(e.target.value)} />
        <Input type="file" placeholder="商品图片" onChange={e => setImage(e.target.value)} />
        <Input type="number" placeholder="商品库存" defaultValue={stock} onChange={e => setStock(e.target.value)} />
        <Input type="text" placeholder="商品分类" defaultValue={tags} onChange={e => setTags(e.target.value)} />
        <Button
          className="w-full"
          onClick={submit}
        >发布</Button>
      </div>
    </div>
  )
}

function FlashSaleApplicationForm({ close, succeed, productId }: { close: () => void, succeed?: () => void, productId: number }) {
  let [discount, setDiscount] = useState('')
  let [stock, setStock] = useState('')
  let [startTime, setStartTime] = useState('')
  let [endTime, setEndTime] = useState('')

  const submit = () => {
    fetch('/api/v1/flashSaleApplication', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${JSON.parse(localStorage.getItem('session') || "").token}`
      },
      body: JSON.stringify({
        productId,
        discount: parseFloat(discount),
        totalQuantity: parseInt(stock),
        startTime,
        endTime
      })
    }).then((data) => {
      close()
    })
  }

  return (
    <div
      style={{ backdropFilter: 'blur(8px)' }}
      onClick={e => { e.stopPropagation(); close() }}
      className="flex flex-col items-center justify-center fixed z-50 bg-[#000000a0] w-full h-full top-0 left-0">
      <div
        onClick={e => e.stopPropagation()}
        className="flex flex-col items-center justify-center m-10 gap-4 bg-white p-10 rounded-lg"
      >
        <Input type="number" placeholder="折扣" defaultValue={discount} onChange={e => setDiscount(e.target.value)} />
        <Input type="number" placeholder="总量" defaultValue={stock} onChange={e => setStock(e.target.value)} />
        <Input type="datetime-local" placeholder="开始时间" defaultValue={startTime} onChange={e => setStartTime(e.target.value)} />
        <Input type="datetime-local" placeholder="结束时间" defaultValue={endTime} onChange={e => setEndTime(e.target.value)} />
        <Button
          className="w-full"
          onClick={submit}
        >申请</Button>
      </div>
    </div>
  )
}

function FlashSaleApplicationItem({ application: { id, discount, totalQuantity, startTime, endTime, status } }: { application: FlashSaleApplication }) {
  return (
    <div className={"group flex h-full w-fit flex-col overflow-hidden border p-3 m-3 rounded shadow"}>
      <div className="flex shrink-0 grow flex-col py-4">
        <div>申请号: {id}</div>
        <div>折扣: {discount}</div>
        <div>总量: {totalQuantity}</div>
        <div>开始时间: {startTime}</div>
        <div>结束时间: {endTime}</div>
        <div>状态: {
          status == 'pending' ? '待审核' :
            status == 'approved' ? '已通过' :
              status == 'rejected' ? '已拒绝' :
                '未知状态'
        }</div>
        {status == 'pending' && <Button>取消申请</Button>}
      </div>
    </div>
  )
}

function fetchData(userId: number) {
  return fetch(`/api/v1/product/seller/${userId}`).then((res) => res.json())
}

function fetchMyApplications(userId: number) {
  return fetch(`/api/v1/flashSaleApplication/applicant/${userId}`).then((res) => res.json())
}

export default MyProducts