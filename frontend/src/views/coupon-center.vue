<template>
  <div class="coupon-center">
    <div class="title">可领取优惠券</div>
    <div class="coupon-list">
      <div class="coupon-item" v-for="item in couponList" :key="item.id">
        <div class="left">
          <div class="money">¥{{item.discount}}</div>
          <div class="desc">满{{item.minSpend}}可用</div>
        </div>
        <div class="right">
          <van-button size="small" type="primary" @click="receiveCoupon(item.id)">领取</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref,onMounted} from 'vue'
import {getCouponCenterApi,receiveApi} from '@/api/coupon'
const couponList=ref([])

const loadData=async()=>{
  const res=await getCouponCenterApi()
  couponList.value=res.data
}
const receiveCoupon=async(id)=>{
  await receiveApi({id})
  alert('领取成功！')
  loadData()
}
onMounted(()=>loadData())
</script>
<style scoped>
.title{padding:14px;font-size:18px;font-weight:bold;}
.coupon-item{
  display:flex;justify-content:space-between;align-items:center;
  margin:10px 14px;padding:16px;background:linear-gradient(135deg,#ff7d00,#ffb347);
  border-radius:10px;color:white;
}
.money{font-size:24px;font-weight:bold}
</style>