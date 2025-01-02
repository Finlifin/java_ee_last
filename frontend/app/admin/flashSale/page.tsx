'use client'
import { Button } from '@/components/ui/button'
import { FlashSaleApplication } from '@/types'
import App from 'next/app'
import React, { startTransition, useEffect } from 'react'

function FlashSaleManagement() {
  let [applications, setApplications] = React.useState<FlashSaleApplication[] | null>(null)
  let [update, setUpdate] = React.useState(0)

  useEffect(() => {
    startTransition(async () => {
      fetchMyApplications().then((applications) => {
        setApplications(applications)
      })
    })
  }, [update])

  return (
    <div>
      <h1 className="text-4xl">秒杀申请</h1>
      {applications === null && <div>loading...</div>}
      {applications && applications?.length == 0 && <div>暂无秒杀申请</div>}
      {applications && applications?.length != 0 && <div>
        {applications?.map((application) => (
          <FlashSaleApplicationItem key={application.id} application={application} succeed={() => setUpdate(update + 1)} />
        ))}
      </div>}
    </div>
  )
}


function FlashSaleApplicationItem({ application: { id, discount, totalQuantity, startTime, endTime, status }, succeed }: { application: FlashSaleApplication, succeed: () => void }) {
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
        {ApproveButton({ applicationId: id, succeed })}
        {RejectButton({ applicationId: id, succeed })}
      </div>
    </div>
  )
}

function ApproveButton({ applicationId, succeed }: { applicationId: number, succeed: () => void }) {
  function approve() {
    fetch(`/api/v1/flashSaleApplication/approve/${applicationId}`,
      {
        method: 'POST'
        , headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${JSON.parse(localStorage.getItem('session') || "").token}`
        }
        ,
      })
      .then(() => {
        alert('已通过')
        window.location.reload()
      })
  }

  return (
    <Button
      onClick={approve}
    >通过</Button>
  )
}

function RejectButton({ applicationId, succeed }: { applicationId: number, succeed: () => void }) {
  function reject() {
    fetch(`/api/v1/flashSaleApplication/reject/${applicationId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${JSON.parse(localStorage.getItem('session') || "").token}`
        },
      })
      .then(() => {
        alert('已拒绝')
        window.location.reload()
      })
  }

  return <Button
    className='my-2'
    onClick={reject}
  >拒绝</Button>
}

function fetchMyApplications() {
  return fetch(`/api/v1/flashSaleApplication`).then((res) => res.json())
}

export default FlashSaleManagement