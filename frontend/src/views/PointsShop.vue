<template>
  <div class="points-shop">
    <div class="my-points">我的积分：{{points}}</div>
    <div class="goods-list">
      <div class="goods-item" v-for="item in goodsList" :key="item.id">
        <div class="name">{{item.name}}</div>
        <div class="need-points">{{item.points}}积分兑换</div>
        <van-button size="small" type="success" @click="exchange(item)">立即兑换</van-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onMounted} from 'vue'
import {getPointsGoodsApi,pointsExchangeApi} from '@/api/points'
const points=ref(0)
const goodsList=ref([])
const load=async()=>{
  const res=await getPointsGoodsApi()
  goodsList.value=res.data
  points.value=res.userPoints
}
const exchange=async(row)=>{
  await pointsExchangeApi({id:row.id})
  alert('兑换成功')
  load()
}
onMounted(load)
</script>
<style scoped>
.my-points{padding:16px;font-size:17px;font-weight:bold;background:#fff}
.goods-item{
  margin:12px 14px;padding:14px;background:#fff;border-radius:8px;
}
.need-points{color:#f56c6c;margin:6px 0;}
</style>