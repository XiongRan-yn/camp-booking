<template>
  <div class="wallet">
    <van-tabs v-model:active="tabVal">
      <van-tab title="未使用" value="unused"/>
      <van-tab title="已使用" value="used"/>
      <van-tab title="已过期" value="expired"/>
    </van-tabs>
    <div class="list">
      <div class="item" v-for="item in listData" :key="item.id">
        <div class="money">¥{{item.discount}}</div>
        <div class="info">
          <div>满{{item.minSpend}}可用</div>
          <div class="time">{{item.validTime}}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onMounted,watch} from 'vue'
import {getMyCouponApi} from '@/api/coupon'
const tabVal=ref('unused')
const listData=ref([])

const load=async()=>{
  const res=await getMyCouponApi({type:tabVal.value})
  listData.value=res.data
}
watch(tabVal,()=>load())
onMounted(load)
</script>
<style scoped>
.item{
  display:flex;gap:16px;
  margin:12px 14px;padding:14px;background:#fff;border-radius:8px
}
.money{font-size:22px;color:#ff7d00;font-weight:bold}
.info{font-size:14px;color:#666}
</style>