'use client'
import { Button } from '@/components/ui/button'
import { useUserInfoStore } from '@/stores/user-info-store'
import Image from 'next/image'

function InfoPanel() {
  const { userInfo } = useUserInfoStore()
  return (
    <div
      className='w-full h-auto bg-white flex flex-col items-center justify-center gap-4 lg:p-20'
    >
      <h1 className='text-4xl'>个人信息</h1>
      <Image src={userInfo?.avatar || "/hero.png"} alt='' width={100} height={100} />
      <div
        className='text-xl'
      >邮件: {userInfo?.email}</div>
      <div
        className='text-xl'
      >用户名: {userInfo?.username}</div>
      <Button className='w-[200px]'>修改密码</Button>
      <Button className='w-[200px]'>修改个人信息</Button>
    </div>
  )
}

export default InfoPanel