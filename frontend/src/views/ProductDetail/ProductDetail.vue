<template>
  <div class="product-detail">
    <!-- 加载中 -->
    <div v-if="loading" class="product-detail__loading">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="!product" class="product-detail__empty">
      <van-empty description="商品不存在或已下架" />
    </div>

    <template v-else>
      <!-- 轮播图 -->
      <van-swiper class="product-detail__swiper" :autoplay="3000" indicator-color="#fff">
        <van-swiper-item v-for="(img, idx) in swiperImages" :key="idx">
          <img class="product-detail__swiper-img" :src="img" alt="商品图" />
        </van-swiper-item>
      </van-swiper>

      <!-- 商品基本信息 -->
      <div class="product-detail__info">
        <div class="product-detail__price-row">
          <span class="product-detail__price-symbol">¥</span>
          <span class="product-detail__price-min">{{ product.minPrice }}</span>
          <span v-if="product.maxPrice && product.maxPrice !== product.minPrice" class="product-detail__price-range">
            ~ ¥{{ product.maxPrice }}
          </span>
          <van-tag
              v-if="product.isFull"
              type="danger"
              size="medium"
              class="product-detail__full-tag"
          >
            已满员
          </van-tag>
        </div>

        <h1 class="product-detail__title">{{ product.title }}</h1>

        <div class="product-detail__tags">
          <van-tag
              v-for="(tag, idx) in product.tags"
              :key="idx"
              plain
              type="primary"
              class="product-detail__tag"
          >
            {{ tag }}
          </van-tag>
        </div>
      </div>

      <!-- 规格入口 -->
      <div class="product-detail__spec-entry" @click="openSpecPopup">
        <span class="product-detail__spec-label">已选</span>
        <span class="product-detail__spec-value">
          {{ selectedSpec ? selectedSpec.name : '请选择规格' }}
        </span>
        <van-icon name="arrow" class="product-detail__spec-arrow" />
      </div>

      <!-- 富文本图文详情 -->
      <div class="product-detail__description">
        <div class="product-detail__desc-title">商品详情</div>
        <div
            class="product-detail__desc-content"
            v-html="product.description || '<p style=&quot;color:#969799;text-align:center;padding:40px 0;&quot;>暂无详情</p>'"
        />
      </div>

      <!-- 底部固定栏 -->
      <div class="product-detail__footer">
        <div class="product-detail__footer-icons">
          <div class="product-detail__footer-icon" @click="handleService">
            <van-icon name="service-o" size="22" />
            <span class="product-detail__footer-label">客服</span>
          </div>
          <div class="product-detail__footer-icon" @click="handleToggleFavorite">
            <van-icon
                :name="isFavorited ? 'star' : 'star-o'"
                size="22"
                :class="{ 'product-detail__footer-icon--active': isFavorited }"
            />
            <span class="product-detail__footer-label">收藏</span>
          </div>
        </div>
        <div class="product-detail__footer-price">
          <span class="product-detail__footer-price-symbol">¥</span>
          <span class="product-detail__footer-price-value">
            {{ selectedSpec ? selectedSpec.price : product.minPrice }}
          </span>
        </div>
        <van-button
            type="primary"
            round
            class="product-detail__footer-btn"
            :disabled="product.isFull"
            @click="openSpecPopup"
        >
          {{ product.isFull ? '已满员' : '立即购买' }}
        </van-button>
      </div>
    </template>

    <!-- 规格选择弹窗 -->
    <SpecPopup
        v-model:visible="specPopupVisible"
        :product="product"
        v-model:selectedSpecId="selectedSpecId"
        v-model:quantity="buyQuantity"
        @confirm="handleSpecConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import { useUserStore } from '@/stores/user';
import { getProductDetail } from '@/api/product';
import { addFavorite, getFavoriteList, removeFavorite } from '@/api/favorite';
import SpecPopup from '@/components/SpecPopup.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(true);
const product = ref(null);
const specPopupVisible = ref(false);
const selectedSpecId = ref(null);
const buyQuantity = ref(1);
const favoriteId = ref(null);

const selectedSpec = computed(() => {
  if (!product.value?.specs) return null;
  return product.value.specs.find((s) => s.id === selectedSpecId.value) || null;
});

const isFavorited = computed(() => product.value?.isFavorited === true);

const swiperImages = computed(() => {
  if (!product.value) return [];
  const imgs = product.value.images && product.value.images.length > 0
      ? product.value.images
      : [product.value.coverImage];
  return imgs.filter(Boolean);
});

onMounted(() => {
  fetchProductDetail();
});

async function fetchProductDetail() {
  loading.value = true;
  try {
    const res = await getProductDetail(route.params.id);
    if (res.code === 0) {
      product.value = res.data;
      // 默认选中第一个有库存的规格
      const defaultSpec = res.data.specs?.find((s) => s.stock > 0);
      if (defaultSpec) {
        selectedSpecId.value = defaultSpec.id;
      }
    } else {
      showToast(res.message || '加载失败');
    }
  } catch (err) {
    showToast('网络异常，请稍后重试');
  } finally {
    loading.value = false;
  }
}

function openSpecPopup() {
  if (product.value.isFull) {
    showToast('该商品已满员');
    return;
  }
  specPopupVisible.value = true;
}

function handleSpecConfirm({ spec, quantity }) {
  if (!userStore.isLoggedIn) {
    showToast('请先登录');
    router.push('/login');
    return;
  }
  // 跳转确认订单页，携带参数
  router.push({
    path: '/checkout',
    query: {
      productId: product.value.id,
      specId: spec.id,
      quantity
    }
  });
}

async function handleToggleFavorite() {
  if (!userStore.isLoggedIn) {
    showToast('请先登录');
    router.push('/login');
    return;
  }

  if (isFavorited.value) {
    // 取消收藏：先查收藏列表找到对应记录ID
    try {
      if (!favoriteId.value) {
        const listRes = await getFavoriteList({ type: targetType(), page: 1, pageSize: 50 });
        if (listRes.code === 0) {
          // 后端 /favorites 返回 Result<List>，data 直接是数组
          const list = Array.isArray(listRes.data) ? listRes.data : listRes.data?.list || [];
          const record = list.find(
              (item) => item.targetId === product.value.id
          );
          if (record) favoriteId.value = record.id;
        }
      }
      if (favoriteId.value) {
        await removeFavorite(favoriteId.value);
        product.value.isFavorited = false;
        favoriteId.value = null;
        showToast('已取消收藏');
      }
    } catch {
      showToast('操作失败');
    }
  } else {
    // 添加收藏
    try {
      const res = await addFavorite({
        targetId: product.value.id,
        targetType: targetType()
      });
      if (res.code === 0) {
        product.value.isFavorited = true;
        showToast('收藏成功');
      } else {
        showToast(res.message || '收藏失败');
      }
    } catch {
      showToast('操作失败');
    }
  }
}

// 收藏 targetType 与后端约定一致：camp / hotel（对应商品 category）
function targetType() {
  return product.value?.category === 'hotel' ? 'hotel' : 'camp';
}

function handleService() {
  showConfirmDialog({
    title: '联系客服',
    message: '客服电话：400-888-8888\n工作时间：9:00-21:00',
    showCancelButton: false,
    confirmButtonText: '知道了'
  }).catch(() => {});
}
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background-color: var(--color-bg, #f7f8fa);
  padding-bottom: 60px;
}

.product-detail__loading {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.product-detail__empty {
  padding: 60px 0;
}

.product-detail__swiper {
  width: 100%;
  height: 375px;
}

.product-detail__swiper-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-detail__info {
  background-color: var(--color-white, #fff);
  padding: var(--spacing-lg, 16px);
}

.product-detail__price-row {
  display: flex;
  align-items: baseline;
}

.product-detail__price-symbol {
  font-size: var(--font-size-lg, 16px);
  font-weight: bold;
  color: var(--color-danger, #ee0a24);
}

.product-detail__price-min {
  font-size: 28px;
  font-weight: bold;
  color: var(--color-danger, #ee0a24);
  margin-left: 2px;
}

.product-detail__price-range {
  font-size: var(--font-size-md, 14px);
  color: var(--color-danger, #ee0a24);
  margin-left: var(--spacing-xs, 4px);
}

.product-detail__full-tag {
  margin-left: var(--spacing-sm, 8px);
}

.product-detail__title {
  font-size: var(--font-size-lg, 16px);
  font-weight: 600;
  color: var(--color-text, #323233);
  margin: var(--spacing-sm, 8px) 0 0;
  line-height: 1.4;
}

.product-detail__tags {
  margin-top: var(--spacing-sm, 8px);
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs, 4px);
}

.product-detail__tag {
  margin-right: 0;
}

.product-detail__spec-entry {
  display: flex;
  align-items: center;
  background-color: var(--color-white, #fff);
  margin-top: var(--spacing-xs, 4px);
  padding: var(--spacing-lg, 16px);
  cursor: pointer;
}

.product-detail__spec-label {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text-light, #969799);
  width: 50px;
  flex-shrink: 0;
}

.product-detail__spec-value {
  flex: 1;
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
}

.product-detail__spec-arrow {
  color: var(--color-text-light, #969799);
  font-size: var(--font-size-sm, 12px);
}

.product-detail__description {
  background-color: var(--color-white, #fff);
  margin-top: var(--spacing-xs, 4px);
  padding: var(--spacing-lg, 16px);
}

.product-detail__desc-title {
  font-size: var(--font-size-lg, 16px);
  font-weight: 600;
  color: var(--color-text, #323233);
  margin-bottom: var(--spacing-md, 12px);
  padding-left: var(--spacing-sm, 8px);
  border-left: 3px solid var(--color-primary, #1989fa);
}

.product-detail__desc-content {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
  line-height: 1.6;
  word-break: break-word;
}

.product-detail__desc-content :deep(img) {
  max-width: 100%;
  border-radius: var(--border-radius, 8px);
}

.product-detail__footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  background-color: var(--color-white, #fff);
  padding: var(--spacing-xs, 4px) var(--spacing-md, 12px);
  padding-bottom: calc(var(--spacing-xs, 4px) + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.product-detail__footer-icons {
  display: flex;
}

.product-detail__footer-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 48px;
  color: var(--color-text, #323233);
  cursor: pointer;
}

.product-detail__footer-icon--active {
  color: var(--color-warning, #ff976a);
}

.product-detail__footer-label {
  font-size: 10px;
  margin-top: 2px;
}

.product-detail__footer-price {
  flex: 1;
  text-align: right;
  padding-right: var(--spacing-md, 12px);
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
}

.product-detail__footer-price-symbol {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-danger, #ee0a24);
  font-weight: bold;
}

.product-detail__footer-price-value {
  font-size: 22px;
  color: var(--color-danger, #ee0a24);
  font-weight: bold;
  margin-left: 1px;
}

.product-detail__footer-btn {
  width: 120px;
  height: 40px;
}
</style>
