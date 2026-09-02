<template>
  <div class="booking-page">
    <van-nav-bar title="报名预约" fixed placeholder />

    <!-- 日期选择 -->
    <div class="booking-date" @click="showDatePicker = true">
      <van-icon name="calendar-o" size="16" color="#1989fa" />
      <span class="booking-date__text">{{ dateText }}</span>
      <van-icon name="arrow-down" size="12" color="#969799" />
    </div>

    <!-- 横向分类滚动 -->
    <div class="booking-cats">
      <div
        v-for="cat in categories"
        :key="cat.key"
        class="booking-cat"
        :class="{ 'booking-cat--active': activeCategory === cat.key }"
        @click="selectCategory(cat.key)"
      >
        {{ cat.label }}
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="booking-loading">
      <van-loading color="#1989fa" vertical>加载中...</van-loading>
    </div>

    <!-- 空状态 -->
    <div v-else-if="filtered.length === 0" class="booking-empty">
      <van-empty description="该分类下暂无商品" />
    </div>

    <!-- 瀑布流（两列卡片） -->
    <div v-else class="booking-grid">
      <div v-for="p in filtered" :key="p.id" class="booking-card" @click="goDetail(p.id)">
        <div class="booking-card__thumb">
          <img v-if="p.coverImage" :src="p.coverImage" class="booking-card__img" alt="" />
          <van-icon v-else name="photo-o" size="48" color="#c8c9cc" />
          <span v-if="p.isFull" class="booking-card__full">满员</span>
        </div>
        <div class="booking-card__body">
          <div class="booking-card__title">{{ p.title }}</div>
          <div class="booking-card__tags">
            <span v-for="(tag, i) in p.tags" :key="i" class="booking-card__tag">{{ tag }}</span>
          </div>
          <div class="booking-card__bottom">
            <div class="booking-card__price">
              <span class="booking-card__symbol">¥</span>{{ formatPrice(p.minPrice) }}<span class="booking-card__unit"> 起</span>
            </div>
            <van-button size="mini" type="primary" round @click.stop="goDetail(p.id)">预约</van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 日期选择弹窗 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round>
      <van-date-picker
        v-model="currentDate"
        title="选择日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

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
const activeCategory = ref("all");
const showDatePicker = ref(false);

// 日期选择：默认今天，范围未来 90 天
const today = new Date();
const minDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
const maxDate = new Date(today.getFullYear(), today.getMonth() + 3, today.getDate());
const currentDate = ref([
  String(minDate.getFullYear()),
  String(minDate.getMonth() + 1).padStart(2, "0"),
  String(minDate.getDate()).padStart(2, "0"),
]);

const categories = [
  { key: "all", label: "全部" },
  { key: "camp", label: "研学营" },
  { key: "hotel", label: "民宿" },
];

const dateText = computed(() => {
  const [y, m, d] = currentDate.value;
  return `${y}年${m}月${d}日`;
});

const filtered = computed(() => {
  return products.value.filter((p) => {
    return activeCategory.value === "all" || p.category === activeCategory.value;
  });
});

function formatPrice(price) {
  if (price === null || price === undefined) return "0";
  const num = Number(price);
  return num % 1 === 0 ? String(num) : num.toFixed(2);
}

function selectCategory(key) {
  activeCategory.value = key;
}

function onDateConfirm({ selectedValues }) {
  currentDate.value = selectedValues;
  showDatePicker.value = false;
}

function goDetail(id) {
  router.push(`/product/${id}`);
}

onMounted(async () => {
  try {
    const res = await getProducts({ page: 1, pageSize: 50 });
    if (res.code === 0) {
      products.value = res.data.list || [];
    }
  } catch (e) {
    // 加载失败保留空列表
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.booking-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 60px;
}

.booking-date {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: #fff;
  font-size: 14px;
  color: #323233;
  cursor: pointer;
}

.booking-date__text {
  flex: 1;
}

.booking-cats {
  display: flex;
  gap: 8px;
  padding: 10px 16px;
  overflow-x: auto;
  background: #fff;
  border-bottom: 1px solid #ebedf0;
}

.booking-cat {
  flex-shrink: 0;
  padding: 6px 20px;
  border-radius: 16px;
  background: #f2f3f5;
  color: #646566;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.booking-cat--active {
  background: #1989fa;
  color: #fff;
  font-weight: 600;
}

.booking-loading,
.booking-empty {
  padding: 80px 0;
}

.booking-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 12px 16px 20px;
}

.booking-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.booking-card__thumb {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 4 / 3;
  background: linear-gradient(135deg, #e8f1fd, #f6f8fb);
  overflow: hidden;
}

.booking-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.booking-card__full {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(238, 10, 24, 0.85);
  color: #fff;
  font-size: 12px;
}

.booking-card__body {
  padding: 10px 12px 12px;
}

.booking-card__title {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 38px;
}

.booking-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 6px;
}

.booking-card__tag {
  padding: 2px 8px;
  border-radius: 6px;
  background: #e8f1fd;
  color: #1989fa;
  font-size: 11px;
}

.booking-card__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.booking-card__price {
  font-size: 16px;
  font-weight: 700;
  color: #ee0a24;
}

.booking-card__symbol {
  font-size: 12px;
}

.booking-card__unit {
  font-size: 12px;
  font-weight: 400;
  color: #969799;
}
</style>
