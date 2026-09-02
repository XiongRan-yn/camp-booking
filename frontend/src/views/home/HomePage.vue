<template>
  <div class="home-page">
    <van-nav-bar title="营地研学 · 民宿预订" fixed placeholder />

    <van-search v-model="keyword" placeholder="搜索营地 / 民宿" />

    <!-- 报名预约入口横幅 -->
    <div class="home-booking-banner" @click="goBooking">
      <van-icon name="calendar-o" size="20" color="#fff" />
      <div class="home-booking-banner__text">
        <div class="home-booking-banner__title">报名预约</div>
        <div class="home-booking-banner__sub">研学营报名 · 民宿预订</div>
      </div>
      <van-icon name="arrow" size="16" color="#fff" />
    </div>

    <div class="category-tabs">
      <div
        v-for="cat in categories"
        :key="cat.key"
        class="category-tab"
        :class="{ 'category-tab--active': activeCategory === cat.key }"
        @click="activeCategory = cat.key"
      >
        {{ cat.label }}
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <van-loading color="#1989fa" vertical>加载中...</van-loading>
    </div>

    <div v-else-if="filtered.length === 0" class="empty-wrap">
      <van-empty description="暂无符合条件的商品" />
    </div>

    <div v-else class="product-grid">
      <div v-for="p in filtered" :key="p.id" class="product-card" @click="goDetail(p.id)">
        <div class="product-card__thumb">
          <van-icon name="photo-o" size="42" color="#c8c9cc" />
          <span v-if="p.isFull" class="product-card__full">满房</span>
        </div>
        <div class="product-card__body">
          <div class="product-card__title">{{ p.title }}</div>
          <div class="product-card__meta">
            <span class="product-card__tag">{{ categoryLabel(p.category) }}</span>
          </div>
          <div class="product-card__price">
            <span class="price-symbol">¥</span>{{ formatPrice(p.minPrice) }}<span class="price-unit"> 起</span>
          </div>
        </div>
      </div>
    </div>

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
import { getProducts } from "@/api/product";

const router = useRouter();

const products = ref([]);
const loading = ref(true);
const keyword = ref("");
const activeCategory = ref("all");

const categories = [
  { key: "all", label: "全部" },
  { key: "camp", label: "研学营" },
  { key: "hotel", label: "民宿" },
];

const filtered = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  return products.value.filter((p) => {
    const matchCat = activeCategory.value === "all" || p.category === activeCategory.value;
    const matchKw = !kw || (p.title || "").toLowerCase().includes(kw);
    return matchCat && matchKw;
  });
});

function categoryLabel(category) {
  return category === "camp" ? "研学营" : category === "hotel" ? "民宿" : "商品";
}

function formatPrice(price) {
  if (price === null || price === undefined) return "0";
  const num = Number(price);
  return num % 1 === 0 ? String(num) : num.toFixed(2);
}

function goDetail(id) {
  router.push(`/product/${id}`);
}

function goBooking() {
  router.push("/booking");
}

onMounted(async () => {
  try {
    const res = await getProducts({ page: 1, pageSize: 50 });
    if (res.code === 0) {
      products.value = res.data.list || [];
    }
  } catch (e) {
    // 加载失败时保留空列表，不阻塞页面
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
}

.category-tabs {
  display: flex;
  gap: 12px;
  padding: 8px 16px;
  overflow-x: auto;
}

.home-booking-banner {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 4px 16px 0;
  padding: 14px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #1989fa, #5babfb);
  color: #fff;
  cursor: pointer;
}

.home-booking-banner__text {
  flex: 1;
}

.home-booking-banner__title {
  font-size: 16px;
  font-weight: 600;
}

.home-booking-banner__sub {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 2px;
}

.category-tab {
  flex-shrink: 0;
  padding: 6px 18px;
  border-radius: 16px;
  background: #f2f3f5;
  color: #646566;
  font-size: 14px;
  cursor: pointer;
}

.category-tab--active {
  background: #1989fa;
  color: #ffffff;
}

.loading-wrap,
.empty-wrap {
  padding: 80px 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 12px 16px 20px;
}

.product-card {
  background: #ffffff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.product-card__thumb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 4 / 3;
  background: linear-gradient(135deg, #e8f1fd, #f6f8fb);
}

.product-card__full {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(238, 10, 24, 0.85);
  color: #ffffff;
  font-size: 12px;
}

.product-card__body {
  padding: 10px 12px 12px;
}

.product-card__title {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-card__meta {
  margin-top: 6px;
}

.product-card__tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 6px;
  background: #e8f1fd;
  color: #1989fa;
  font-size: 12px;
}

.product-card__price {
  margin-top: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #ee0a24;
}

.price-symbol {
  font-size: 12px;
}

.price-unit {
  font-size: 12px;
  font-weight: 400;
  color: #969799;
}
</style>
