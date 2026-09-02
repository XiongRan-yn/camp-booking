<template>
  <div class="coupon-wallet-page">
    <van-nav-bar title="卡券包" fixed placeholder />
    <van-tabs v-model:active="activeTab">
      <van-tab title="未使用" value="usable" />
      <van-tab title="已使用" value="used" />
      <van-tab title="已过期" value="expired" />
    </van-tabs>

    <div class="coupon-list">
      <div v-for="item in filteredList" :key="item.id" class="coupon-card">
        <div class="coupon-left">
          <div class="coupon-money"><span>¥</span>{{ moneyText(item) }}</div>
          <div class="coupon-condition">{{ conditionText(item) }}</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-title">{{ item.couponTitle }}</div>
          <div class="coupon-time">有效期至 {{ formatTime(item.expireAt) }}</div>
          <van-tag :type="tagType(item)">{{ tagText(item) }}</van-tag>
        </div>
      </div>
      <van-empty v-if="filteredList.length === 0" description="暂无卡券" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import { getMyCoupons } from "@/api/coupon";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const activeTab = ref("usable");
const allCoupons = ref([]);

const now = () => new Date();

function isExpired(item) {
  return item.expireAt && new Date(item.expireAt) < now();
}

const filteredList = computed(() => {
  const list = allCoupons.value;
  if (activeTab.value === "used") return list.filter((i) => i.status === "used");
  if (activeTab.value === "expired") return list.filter((i) => isExpired(i));
  return list.filter((i) => i.status === "usable" && !isExpired(i));
});

function moneyText(item) {
  if (item.type === "discount") return (item.discountValue * 10).toFixed(1) + "折";
  return item.discountValue;
}

function conditionText(item) {
  if (item.type === "discount") return "折扣券";
  return item.minAmount > 0 ? `满${item.minAmount}可用` : "无门槛";
}

function formatTime(t) {
  return t ? String(t).slice(0, 10) : "长期";
}

function tagText(item) {
  if (isExpired(item)) return "已过期";
  return item.status === "used" ? "已使用" : "可使用";
}

function tagType(item) {
  if (isExpired(item)) return "default";
  return item.status === "used" ? "warning" : "success";
}

async function loadCoupons() {
  if (!userStore.isLoggedIn) {
    showToast("请先登录");
    router.push({ path: "/login", query: { redirect: "/coupon-wallet" } });
    return;
  }
  try {
    const res = await getMyCoupons();
    if (res.code === 0) {
      allCoupons.value = res.data || [];
    }
  } catch (e) {
    showToast("加载失败");
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