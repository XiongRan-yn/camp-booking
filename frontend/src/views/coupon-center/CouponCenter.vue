<template>
  <div class="coupon-center-page">
    <van-nav-bar title="领券中心" fixed placeholder />
    <div class="coupon-list">
      <div v-for="item in couponList" :key="item.id" class="coupon-card">
        <div class="coupon-left">
          <div class="coupon-money"><span>¥</span>{{ moneyText(item) }}</div>
          <div class="coupon-condition">{{ conditionText(item) }}</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-title">{{ item.title }}</div>
          <div class="coupon-time">有效期至 {{ formatTime(item.endTime) }}</div>
          <van-button
            size="small"
            :type="item.hasReceived ? 'default' : 'primary'"
            :disabled="item.hasReceived"
            @click="handleReceive(item)"
          >
            {{ item.hasReceived ? "已领取" : "立即领取" }}
          </van-button>
        </div>
      </div>
      <van-empty v-if="couponList.length === 0" description="暂无可领优惠券" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { showToast } from "vant";
import { getCouponList, receiveCoupon } from "@/api/coupon";

const couponList = ref([]);

function moneyText(item) {
  if (item.type === "discount") {
    return (item.discountValue * 10).toFixed(1) + "折";
  }
  return item.discountValue;
}

function conditionText(item) {
  if (item.type === "discount") return "折扣券";
  return item.minAmount > 0 ? `满${item.minAmount}可用` : "无门槛";
}

function formatTime(t) {
  return t ? String(t).slice(0, 10) : "长期";
}

async function loadCoupons() {
  try {
    const res = await getCouponList();
    if (res.code === 0) {
      couponList.value = res.data || [];
    }
  } catch (e) {
    showToast("加载失败");
  }
}

async function handleReceive(item) {
  try {
    const res = await receiveCoupon(item.id);
    if (res.code === 0) {
      showToast("领取成功");
      loadCoupons();
    } else {
      showToast(res.message || "领取失败");
    }
  } catch (e) {
    showToast("领取失败");
  }
}

onMounted(loadCoupons);
</script>

<style scoped>
.coupon-list {
  padding: 12px 0;
}
.coupon-card {
  display: flex;
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 10px;
  overflow: hidden;
}
.coupon-left {
  width: 110px;
  background: linear-gradient(135deg, #ff6034, #ff8934);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
}
.coupon-money {
  font-size: 24px;
  font-weight: bold;
}
.coupon-money span {
  font-size: 13px;
}
.coupon-condition {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.9;
}
.coupon-right {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.coupon-title {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
  align-self: flex-start;
}
.coupon-time {
  font-size: 12px;
  color: #969799;
  margin: 6px 0 10px;
  align-self: flex-start;
}
</style>