<template>
  <div class="page-container settings-page">
    <van-nav-bar title="设置" left-arrow fixed placeholder @click-left="goBack" />

    <van-cell-group inset class="settings-group">
      <van-cell title="账号与安全" icon="shield-o" is-link @click="showComingSoon" />
      <van-cell title="消息通知" icon="bell-o" is-link @click="showComingSoon" />
      <van-cell title="隐私设置" icon="lock-o" is-link @click="showComingSoon" />
      <van-cell title="清除缓存" icon="delete-o" is-link @click="clearCache" />
    </van-cell-group>

    <van-cell-group inset class="settings-group">
      <van-cell title="关于我们" icon="info-o" is-link @click="showAbout" />
      <van-cell title="检查更新" icon="replay" is-link @click="showComingSoon" />
    </van-cell-group>

    <div v-if="isLoggedIn" class="settings-logout">
      <van-button block round type="danger" @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { showToast, showConfirmDialog, showDialog } from "vant";
import { useUserStore } from "@/stores/user";
import "vant/lib/index.css";

const router = useRouter();
const userStore = useUserStore();
const isLoggedIn = computed(() => userStore.isLoggedIn);

function goBack() {
  router.back();
}

function showComingSoon() {
  showToast("功能开发中，敬请期待");
}

function clearCache() {
  showToast("缓存已清除");
}

function showAbout() {
  showDialog({
    title: "关于我们",
    message: "研学营 / 民宿预订系统\n版本 v1.0.0\n大一 Web 开发课题 · 7 人小组",
  });
}

function handleLogout() {
  showConfirmDialog({
    title: "退出登录",
    message: "确定要退出当前账号吗？",
  })
    .then(() => {
      userStore.logout();
      showToast("已退出登录");
      router.replace("/mine");
    })
    .catch(() => {
      // 用户取消退出
    });
}
</script>

<style scoped>
.settings-page {
  padding-bottom: 40px;
}

.settings-group {
  margin-top: var(--spacing-md);
}

.settings-logout {
  margin: var(--spacing-xl) var(--spacing-md);
}
</style>