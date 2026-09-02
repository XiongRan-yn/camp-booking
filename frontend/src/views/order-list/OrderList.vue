<template>
  <div class="order-page">
    <van-nav-bar title="我的订单" fixed placeholder />
    <van-tabs v-model:active="activeTab" @change="onRefresh">
      <van-tab title="全部" name="" />
      <van-tab title="待支付" name="pending" />
      <van-tab title="已支付" name="paid" />
      <van-tab title="已完成" name="completed" />
      <van-tab title="已取消" name="cancelled" />
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
        <div v-if="item.status === 'paid'" class="order-actions">
          <van-button size="small" type="success" @click="handleComplete(item)">确认完成</van-button>
        </div>
        <div v-if="item.status === 'completed'" class="order-actions">
          <van-button size="small" type="primary" @click="openReview(item)">去评价</van-button>
        </div>
      </div>
      <van-empty v-if="!loading && orderList.length === 0" description="暂无订单" />
    </van-list>

    <!-- 评价弹窗 -->
    <van-popup v-model:show="reviewVisible" position="bottom" round>
      <div class="review-popup">
        <div class="review-popup__header">
          <span class="review-popup__title">评价订单</span>
          <van-icon name="cross" size="18" @click="reviewVisible = false" />
        </div>
        <div class="review-popup__product">{{ currentReview.productTitle }}</div>
        <div class="review-popup__rate">
          <span class="review-popup__label">评分</span>
          <van-rate v-model="reviewForm.rating" size="24" color="#ffb400" />
        </div>
        <van-field
          v-model="reviewForm.content"
          type="textarea"
          rows="3"
          autosize
          maxlength="200"
          show-word-limit
          placeholder="说说你的体验吧（1-200字）"
        />
        <div class="review-popup__actions">
          <van-button size="small" @click="reviewVisible = false">取消</van-button>
          <van-button size="small" type="primary" :loading="reviewSubmitting" @click="submitReview">提交评价</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { showToast } from "vant";
import { getOrderList, payOrder, cancelOrder, completeOrder } from "@/api/order";
import { submitReview as submitReviewApi } from "@/api/review";

const activeTab = ref("");
const keyword = ref("");
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const orderList = ref([]);

const reviewVisible = ref(false);
const reviewSubmitting = ref(false);
const currentReview = ref({});
const reviewForm = reactive({ rating: 5, content: "" });

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

async function handleComplete(item) {
  try {
    const res = await completeOrder(item.id);
    if (res.code === 0) {
      showToast("订单已完成");
      onRefresh();
    } else {
      showToast(res.message || "操作失败");
    }
  } catch (e) {
    showToast("操作失败");
  }
}

function openReview(item) {
  currentReview.value = item;
  reviewForm.rating = 5;
  reviewForm.content = "";
  reviewVisible.value = true;
}

async function submitReview() {
  const content = (reviewForm.content || "").trim();
  if (!content) {
    showToast("请填写评价内容");
    return;
  }
  // 评价 type 映射：民宿→hotel，研学营→dynamic（后端约束 hotel|dynamic）
  const type = currentReview.value.category === "hotel" ? "hotel" : "dynamic";
  reviewSubmitting.value = true;
  try {
    const res = await submitReviewApi({
      type,
      productId: currentReview.value.productId,
      orderId: currentReview.value.id,
      rating: reviewForm.rating,
      content,
    });
    if (res.code === 0) {
      showToast("评价成功");
      reviewVisible.value = false;
      onRefresh();
    } else {
      showToast(res.message || "评价失败");
    }
  } catch (e) {
    showToast(e?.message || "评价失败");
  } finally {
    reviewSubmitting.value = false;
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
.review-popup {
  padding: 20px 16px 24px;
}
.review-popup__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.review-popup__title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}
.review-popup__product {
  font-size: 13px;
  color: #969799;
  margin-bottom: 12px;
}
.review-popup__rate {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.review-popup__label {
  font-size: 14px;
  color: #646566;
}
.review-popup__actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}
</style>