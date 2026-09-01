<template>
  <div class="order-page">
    <van-tabs v-model:active="activeTab">
      <van-tab title="全部" value="" />
      <van-tab title="待支付" value="pending" />
      <van-tab title="已支付" value="paid" />
      <van-tab title="已取消" value="cancelled" />
    </van-tabs>

    <div class="search-wrap">
      <van-search
        v-model="keyword"
        placeholder="搜索订单商品名称"
        @search="onSearch"
      />
    </div>

    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多订单了"
      @load="loadOrderList"
    >
      <div class="order-item" v-for="item in orderList" :key="item.id">
        <div class="order-title">订单号：{{item.orderNo}}</div>
        <div class="order-goods">{{item.goodsName}}</div>
        <div class="order-price">¥{{item.price}}</div>
        <div class="order-status">{{item.statusText}}</div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getOrderListApi } from '@/api/order'

const activeTab = ref('')
const keyword = ref('')
const loading = ref(false)
const finished = ref(false)
const orderList = ref([])
let page = 1

const loadOrderList = async () => {
  loading.value = true
  try {
    const res = await getOrderListApi({
      page,
      keyword: keyword.value,
      status: activeTab.value
    })
    if(res.data.length){
      orderList.value.push(...res.data)
      page++
    }else{
      finished.value = true
    }
  }catch(err){
    console.log('订单加载失败',err)
  }
  loading.value = false
}
const onSearch = () => {
  page = 1
  finished.value = false
  orderList.value = []
  loadOrderList()
}
</script>

<style scoped>
.search-wrap{
  padding:12px;
}
.order-item{
  margin:10px 12px;
  padding:14px;
  background:#fff;
  border-radius:8px;
}
.order-title{
  font-size:14px;color:#666;
}
.order-goods{
  margin:6px 0;font-size:16px;
}
.order-price{
  color:#f56c6c;font-weight:bold;
}
</style>