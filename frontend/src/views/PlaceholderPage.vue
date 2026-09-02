<template>
  <div class="placeholder-page">
    <van-nav-bar title="模块开发中" fixed placeholder />
    <div class="placeholder-page__body">
      <van-icon name="clock-o" size="64" color="#c8c9cc" />
      <p class="placeholder-page__text">该模块还在开发中，暂时无法访问</p>
      <p class="placeholder-page__sub">{{ owner }}</p>
      <van-button type="primary" plain size="small" @click="$router.push('/mine')">返回「我的」</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const owner = computed(() => route.meta.owner || "待交付");
</script>

<style scoped>
.placeholder-page__body {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 24px;
  text-align: center;
}

.placeholder-page__text {
  color: #646566;
  font-size: 15px;
}

.placeholder-page__sub {
  color: #969799;
  font-size: 13px;
}
</style>