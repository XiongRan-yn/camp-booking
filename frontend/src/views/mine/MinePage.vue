<template>
  <div class="page-container mine-page">
    <van-nav-bar title="我的" fixed placeholder />

    <!-- 用户信息卡片 -->
    <div class="user-card" :class="{ 'user-card--login': !isLoggedIn }" @click="!isLoggedIn && go('/login')">
      <div class="user-card__avatar">
        <img v-if="userAvatar" :src="userAvatar" alt="头像" />
        <van-icon v-else name="user-o" size="28" color="#ffffff" />
      </div>
      <div class="user-card__info">
        <template v-if="isLoggedIn">
          <div class="user-card__name">{{ nickname }}</div>
          <div class="user-card__sub">积分余额：{{ pointsBalance }}</div>
        </template>
        <template v-else>
          <div class="user-card__name">未登录</div>
          <div class="user-card__sub">点击登录 / 注册，同步收藏与订单</div>
        </template>
      </div>
    </div>

    <!-- 功能栏 -->
    <van-grid :column-num="4" :border="false" class="function-grid">
      <van-grid-item icon="star-o" text="我的收藏" @click="go('/favorites')" />
      <van-grid-item icon="comment-o" text="我的评价" @click="go('/reviews')" />
      <van-grid-item icon="contact-o" text="出行人" @click="go('/traveler-info')" />
      <van-grid-item icon="orders-o" text="我的订单" @click="go('/order-list')" />
      <van-grid-item icon="coupon-o" text="卡券包" @click="go('/coupon-wallet')" />
      <van-grid-item icon="points" text="积分商城" @click="go('/points-mall')" />
      <van-grid-item icon="balance-o" text="领券中心" @click="go('/coupon-center')" />
      <van-grid-item icon="gift-o" text="兑换中心" @click="go('/redeem')" />
    </van-grid>

    <!-- 工具菜单 -->
    <van-cell-group inset class="tool-menu">
      <van-cell title="设置" icon="setting-o" is-link @click="go('/settings')" />
      <van-cell title="反馈投诉" icon="warning-o" is-link @click="go('/feedback')" />
    </van-cell-group>

    <!-- 底部 TabBar -->
        <van-tabbar route fixed placeholder>
      <van-tabbar-item replace to="/home" icon="wap-home-o">首页</van-tabbar-item>
      <van-tabbar-item replace to="/booking" icon="calendar-o">预约</van-tabbar-item>
      <van-tabbar-item replace to="/mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import request from "@/api/request";
import { useUserStore } from "@/stores/user";
import "vant/lib/index.css";

const router = useRouter();
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn);
const nickname = computed(() => userStore.userInfo?.nickname || "用户");
const userAvatar = computed(() => userStore.userInfo?.avatar || "");
const pointsBalance = ref(0);

function go(path) {
  router.push(path);
}

async function fetchUserInfo() {
  try {
    const res = await request.get("/auth/me");
    if (res.code === 0) {
      userStore.setUser(res.data);
      pointsBalance.value = res.data.pointsBalance || 0;
    }
  } catch (error) {
    // 接口未就绪时静默处理，保留本地登录态
  }
}

onMounted(() => {
  if (isLoggedIn.value) {
    fetchUserInfo();
  }
});
</script>

<style scoped>
.mine-page {
  padding-bottom: 60px;
}

.user-card--login { cursor: pointer; }
.user-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin: var(--spacing-lg) var(--spacing-md);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius);
  background: linear-gradient(135deg, #1989fa, #5babfb);
  color: #ffffff;
}

.user-card__avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  overflow: hidden;
}

.user-card__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-card__name {
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.user-card__sub {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-sm);
  opacity: 0.9;
}

.function-grid {
  margin: var(--spacing-md);
  border-radius: var(--border-radius);
  overflow: hidden;
}

.tool-menu {
  margin: var(--spacing-md);
}
</style>