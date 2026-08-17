<template>
  <div class="product-detail-page">
    <!-- 轮播图 -->
    <van-swipe class="detail-swiper" :autoplay="3000">
      <van-swipe-item v-for="img in detailData.images" :key="img">
        <img class="swiper-img" :src="img" alt="">
      </van-swipe-item>
    </van-swipe>

    <div class="detail-content">
      <h1 class="detail-title">{{ detailData.title }}</h1>
      <div class="detail-price">
        <span class="unit">¥</span>
        <span class="num">{{ detailData.minPrice }}</span>
        <span class="tip">起</span>
      </div>
      <div class="tag-group">
        <van-tag v-for="tag in detailData.tags" :key="tag" size="small">
          {{ tag }}
        </van-tag>
      </div>

      <!-- 富文本详情 -->
      <div class="rich-html" v-html="detailData.description"></div>
    </div>

    <!-- 底部固定操作栏 -->
    <div class="detail-bottom-bar">
      <div class="bar-left">
        <van-icon name="service" size="22" />
        <van-icon
          :name="detailData.isFavorited ? 'star' : 'star-o'"
          color="#ee0a24"
          size="22"
          @click="handleToggleFavorite"
        />
      </div>
      <div class="bar-right">
        <van-button type="warning" @click="openSpecPopup = true">立即购买</van-button>
      </div>
    </div>

    <!-- 规格弹窗组件 -->
    <SpecPopup
      v-model:show="openSpecPopup"
      :product-info="detailData"
      @confirm="onSpecConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import SpecPopup from '@/components/SpecPopup.vue'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const productId = route.params.id

const detailData = ref({})
const openSpecPopup = ref(false)

// 获取商品详情
async function fetchDetail() {
  const res = await request.get(`/products/${productId}`)
  detailData.value = res.data
}

// 收藏切换
async function handleToggleFavorite() {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    return router.push('/login')
  }
  try {
    if (detailData.value.isFavorited) {
      // 这里简化，实际需要拿到收藏id，可根据业务调整
      showToast('取消收藏')
    } else {
      await request.post('/favorites', { targetId: productId, targetType: 'product' })
      showToast('收藏成功')
    }
    detailData.value.isFavorited = !detailData.value.isFavorited
  } catch (e) {
    showToast(e?.message || '操作失败')
  }
}

// 规格弹窗确认，跳转到确认订单页，携带参数
function onSpecConfirm(opt) {
  router.push({
    path: '/checkout',
    query: {
      productId: productId,
      specId: opt.specId,
      specName: opt.specName,
      price: opt.price,
      quantity: opt.quantity
    }
  })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.product-detail-page {
  padding-bottom: 60px;
  background: var(--color-bg);
}
.detail-swiper {
  height: 300px;
}
.swiper-img {
  width:100%;
  height:100%;
  object-fit: cover;
}
.detail-content {
  padding: var(--spacing-lg);
  background: #fff;
}
.detail-title {
  font-size: var(--font-size-xl);
  color: var(--color-text);
  margin: 0 0 var(--spacing-sm);
}
.detail-price {
  color: var(--color-danger);
  font-weight: bold;
  margin-bottom: var(--spacing-sm);
}
.detail-price .num {
  font-size:24px;
}
.tag-group {
  display:flex;
  gap: 6px;
  margin-bottom: var(--spacing-lg);
}
.rich-html {
  margin-top: var(--spacing-lg);
}
/*底部固定栏*/
.detail-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right:0;
  height: 54px;
  background:#fff;
  display:flex;
  align-items:center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
}
.bar-left {
  display:flex;
  gap:24px;
}
</style>