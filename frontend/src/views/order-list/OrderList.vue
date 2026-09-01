<template>
  <div class="order-page">
    <van-nav-bar title="我的订单" fixed placeholder />
    <van-tabs v-model:active="activeTab" @change="onRefresh">
      <van-tab title="全部" value="" />
      <van-tab title="待支付" value="pending" />
      <van-tab title="已支付" value="paid" />
      <van-tab title="已取消" value="cancelled" />
    </van-tabs>

    <div class="search-wrap">
      <van-search v-model="keyword" placeholder="搜索商品名称" @search="onRefresh" />
    </div>

    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多订单了" @load="loadOrders">
      <div v-for="item in orderList" :key="item.id" class="order-item">
        <div class="order-header">
          <span class="order-no">订单号：{{ item.orderNo }}</span>
          <van-tag :type="tagType(item.status)">{{ statusText(item.status) }}</van-tag>
        </div>
        <div class="order-body">
          <img v-if="item.productImage" :src="item.productImage" class="order-img" alt="" />
          <div class="order-info">
            <div class="order-title">{{ item.productTitle }}</div>
            <div class="order-spec">{{ item.specName }} × {{ item.quantity }}</div>
          </div>
          <div class="order-price">¥{{ item.actualPrice }}</div>
        </div>
        <div v-if="item.status === 'pending'" class="order-actions">
          <van-button size="small" @click="handleCancel(item)">取消订单</van-button>
          <van-button size="small" type="primary" @click="handlePay(item)">去支付</van-button>
        </div>
      </div>
      <van-empty v-if="!loading && orderList.length === 0" description="暂无订单" />
    </van-list>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { showToast } from "vant";
import { getOrderList, payOrder, cancelOrder } from "@/api/order";

const activeTab = ref("");
const keyword = ref("");
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const orderList = ref([]);

const statusMap = { pending: "待支付", paid: "已支付", cancelled: "已取消", completed: "已完成" };
const tagMap = { pending: "warning", paid: "success", cancelled: "default", completed: "primary" };

function statusText(s) {
  return statusMap[s] || s || "未知";
}
function tagType(s) {
  return tagMap[s] || "default";
}

async function loadOrders() {
  try {
    const res = await getOrderList({
      page: page.value,
      pageSize: 10,
      status: activeTab.value,
      keyword: keyword.value,
    });
    if (res.code === 0) {
      const data = res.data || {};
      const list = data.list || [];
      orderList.value.push(...list);
      if (orderList.value.length >= (data.total || 0)) {
        finished.value = true;
      } else {
        page.value += 1;
      }
    }
  } catch (e) {
    showToast("加载失败");
  } finally {
    loading.value = false;
  }
}

function onRefresh() {
  page.value = 1;
  finished.value = false;
  orderList.value = [];
  loadOrders();
}

async function handlePay(item) {
  try {
    const res = await payOrder(item.id);
    if (res.code === 0) {
      showToast("支付成功");
      onRefresh();
    } else {
      showToast(res.message || "支付失败");
    }
  } catch (e) {
    showToast("支付失败");
  }
}

async function handleCancel(item) {
  try {
    const res = await cancelOrder(item.id);
    if (res.code === 0) {
      showToast("已取消");
      onRefresh();
    } else {
      showToast(res.message || "取消失败");
    }
  } catch (e) {
    showToast("取消失败");
  }
}
</script>

<style scoped>
.search-wrap {
  padding: 8px 12px;
}
.order-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  padding: 12px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.order-no {
  font-size: 12px;
  color: #969799;
}
.order-body {
  display: flex;
  align-items: center;
  gap: 10px;
}
.order-img {
  width: 64px;
  height: 64px;
  border-radius: 6px;
  object-fit: cover;
  background: #f2f3f5;
}
.order-info {
  flex: 1;
}
.order-title {
  font-size: 15px;
  color: #323233;
}
.order-spec {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
}
.order-price {
  font-size: 16px;
  font-weight: 600;
  color: #ee0a24;
}
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
}
</style>