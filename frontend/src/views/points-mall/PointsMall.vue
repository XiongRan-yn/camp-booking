<template>
  <div class="points-mall-page">
    <van-nav-bar title="积分商城" fixed placeholder />

    <div class="points-header">
      <div class="points-balance">
        <div class="points-num">{{ balance }}</div>
        <div class="points-label">我的积分</div>
      </div>
      <div class="points-stats">
        <div class="stat">累计获得 <b>{{ totalEarned }}</b></div>
        <div class="stat">累计消耗 <b>{{ totalSpent }}</b></div>
      </div>
    </div>

    <van-notice-bar left-icon="volume-o" text="商品兑换即将上线，当前可用积分兑换优惠券/免房券（兑换中心）" />

    <van-cell title="去兑换中心" icon="gift-o" is-link to="/redeem" />

    <van-tabs v-model:active="activeTab">
      <van-tab title="积分明细" name="records" />
    </van-tabs>

    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多记录了" @load="loadRecords">
      <div v-for="item in recordList" :key="item.id" class="record-item">
        <div class="record-left">
          <div class="record-remark">{{ item.remark || item.source }}</div>
          <div class="record-time">{{ formatTime(item.createdAt) }}</div>
        </div>
        <div class="record-amount" :class="item.type === 'spend' ? 'minus' : 'plus'">
          {{ item.type === "spend" ? "-" : "+" }}{{ item.amount }}
        </div>
      </div>
      <van-empty v-if="!loading && recordList.length === 0" description="暂无积分记录" />
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { showToast } from "vant";
import { getPointsBalance, getPointsRecords } from "@/api/points";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const balance = ref(0);
const totalEarned = ref(0);
const totalSpent = ref(0);
const activeTab = ref("records");
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const recordList = ref([]);

function formatTime(t) {
  return t ? String(t).slice(0, 16).replace("T", " ") : "";
}

async function loadBalance() {
  try {
    const res = await getPointsBalance();
    if (res.code === 0) {
      balance.value = res.data.balance || 0;
      totalEarned.value = res.data.totalEarned || 0;
      totalSpent.value = res.data.totalSpent || 0;
    }
  } catch (e) {
    // 静默处理，未登录也能看页面
  }
}

async function loadRecords() {
  if (!userStore.isLoggedIn) {
    finished.value = true;
    loading.value = false;
    return;
  }
  try {
    const res = await getPointsRecords({ page: page.value, pageSize: 20 });
    if (res.code === 0) {
      const data = res.data || {};
      const list = data.list || [];
      recordList.value.push(...list);
      if (recordList.value.length >= (data.total || 0)) {
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

onMounted(() => {
  loadBalance();
  loadRecords();
});
</script>

<style scoped>
.points-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  background: linear-gradient(135deg, #1989fa, #5babfb);
  color: #fff;
}
.points-balance {
  text-align: center;
}
.points-num {
  font-size: 36px;
  font-weight: bold;
}
.points-label {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 2px;
}
.points-stats {
  font-size: 13px;
  opacity: 0.95;
  text-align: right;
}
.stat {
  margin: 4px 0;
}
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  margin: 10px 12px;
  padding: 12px;
  border-radius: 8px;
}
.record-remark {
  font-size: 15px;
  color: #323233;
}
.record-time {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
}
.record-amount {
  font-size: 18px;
  font-weight: bold;
}
.record-amount.plus {
  color: #ee0a24;
}
.record-amount.minus {
  color: #1989fa;
}
</style>