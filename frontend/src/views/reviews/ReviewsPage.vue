<template>
  <div class="page-container reviews-page">
    <van-nav-bar title="我的评价" left-arrow fixed placeholder @click-left="goBack" />

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="我的动态" />
      <van-tab title="酒店评价" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-for="item in list" :key="item.id" class="review-item">
          <div class="review-item__header">
            <span class="review-item__nickname">{{ item.nickname || "匿名用户" }}</span>
            <van-rate
              v-model="item.rating"
              readonly
              size="14"
              color="#ffb400"
            />
          </div>
          <div class="review-item__content">{{ item.content }}</div>
          <div v-if="item.images && item.images.length" class="review-item__images">
            <van-image
              v-for="(img, index) in item.images"
              :key="index"
              width="80"
              height="80"
              radius="6"
              fit="cover"
              :src="img"
            />
          </div>
          <div class="review-item__time">{{ formatTime(item.createdAt) }}</div>
        </div>
        <van-empty v-if="finished && list.length === 0" description="还没有评价" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import request from "@/api/request";
import "vant/lib/index.css";

const router = useRouter();
const TAB_TYPES = ["", "hotel"];
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

function formatTime(value) {
  if (!value) return "";
  return String(value).slice(0, 16).replace("T", " ");
}

async function onLoad() {
  try {
    const res = await request.get("/reviews", {
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
</script>

<style scoped>
.reviews-page {
  padding-bottom: 40px;
}

.review-item {
  margin: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.review-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.review-item__nickname {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--color-text);
}

.review-item__content {
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-md);
  line-height: 1.6;
  color: var(--color-text);
  word-break: break-all;
}

.review-item__images {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
  flex-wrap: wrap;
}

.review-item__time {
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-xs);
  color: var(--color-text-light);
}
</style>