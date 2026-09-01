<template>
  <div class="record-page">
    <div class="record-item" v-for="item in recordList" :key="item.id">
      <div class="goods-name">{{item.goodsName}}</div>
      <div class="row">
        <span>消耗积分：{{item.points}}</span>
        <span>{{item.createTime}}</span>
      </div>
    </div>
    <div v-if="recordList.length===0" class="empty">暂无兑换记录</div>
  </div>
</template>
<script setup>
import {ref,onMounted} from 'vue'
import {getExchangeRecordApi} from '@/api/points'
const recordList=ref([])
const load=async()=>{
  const res=await getExchangeRecordApi()
  recordList.value=res.data
}
onMounted(load)
</script>
<style scoped>
.record-item{
  padding:14px;margin:10px 14px;background:#fff;border-radius:8px;
}
.goods-name{font-size:16px;margin-bottom:6px}
.row{display:flex;justify-content:space-between;font-size:13px;color:#777}
.empty{text-align:center;padding:40px;color:#999}
</style>