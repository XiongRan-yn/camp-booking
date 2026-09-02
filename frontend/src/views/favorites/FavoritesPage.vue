<template>
  <div class="page-container favorites-page">
    <van-nav-bar title="我的收藏" left-arrow fixed placeholder @click-left="goBack" />

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="全部" />
      <van-tab title="研学营" />
      <van-tab title="民宿" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-for="item in list" :key="item.id" class="favorite-item">
          <van-image
            width="80"
            height="80"
            radius="8"
            fit="cover"
            :src="item.targetImage"
          />
          <div class="favorite-item__info">
            <div class="favorite-item__title text-ellipsis">{{ item.targetTitle }}</div>
            <div class="favorite-item__price">¥{{ formatPrice(item.targetPrice) }}</div>
            <div class="favorite-item__time">{{ formatTime(item.createdAt) }}</div>
          </div>
          <van-button size="small" plain type="danger" @click="handleRemove(item)">
            取消收藏
          </van-button>
        </div>
        <van-empty v-if="finished && list.length === 0" description="暂无收藏，快去逛逛吧" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { showToast, showConfirmDialog } from "vant";
import request from "@/api/request";
import "vant/lib/index.css";

const router = useRouter();
const TAB_TYPES = ["", "camp", "hotel"];
const pageSize = 10;

const activeTab = ref(0);
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const page = ref(1);

function goBack() {
  router.back();
}

function getType() {
  return TAB_TYPES[activeTab.value];
}

function formatPrice(value) {
  if (value === null || value === undefined) return "--";
  return Number(value).toFixed(2);
}

function formatTime(value) {
  if (!value) return "";
  return String(value).slice(0, 16).replace("T", " ");
}

async function onLoad() {
  try {
    const res = await request.get("/favorites", {
      params: { type: getType(), page: page.value, pageSize },
    });
    if (res.code === 0) {
      const data = res.data;
      const items = Array.isArray(data) ? data : data?.list || [];
      list.value.push(...items);
      const total = typeof data === "number" ? data : data?.total ?? 0;
      finished.value = list.value.length >= total || items.length < pageSize;
      page.value += 1;
    } else {
      showToast(res.message || "加载失败");
      finished.value = true;
    }
  } catch (error) {
    showToast(error.message || "加载失败");
    finished.value = true;
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
}

function onRefresh() {
  finished.value = false;
  loading.value = true;
  page.value = 1;
  list.value = [];
  onLoad();
}

function onTabChange() {
  finished.value = false;
  loading.value = false;
  page.value = 1;
  list.value = [];
}

function handleRemove(item) {
  showConfirmDialog({
    title: "取消收藏",
    message: `确定要取消收藏「${item.targetTitle}」吗？`,
  })
    .then(async () => {
      try {
        const res = await request.delete(`/favorites/${item.id}`);
        if (res.code === 0) {
          showToast("已取消收藏");
          list.value = list.value.filter((it) => it.id !== item.id);
        } else {
          showToast(res.message || "操作失败");
        }
      } catch (error) {
        showToast(error.message || "操作失败");
      }
    })
    .catch(() => {
      // 用户取消
    });
}
</script>

<style scoped>
.favorites-page {
  padding-bottom: 40px;
}

.favorite-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.favorite-item__info {
  flex: 1;
  min-width: 0;
}

.favorite-item__title {
  font-size: var(--font-size-md);
  color: var(--color-text);
}

.favorite-item__price {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-danger);
}

.favorite-item__time {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-xs);
  color: var(--color-text-light);
}
</style>