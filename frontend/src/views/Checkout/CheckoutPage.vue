<template>
  <div class="checkout">
    <!-- 加载中 -->
    <div v-if="loading" class="checkout__loading">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <template v-else>
      <!-- 商品快照 -->
      <div class="checkout__card">
        <img class="checkout__product-img" :src="productImage" alt="商品图" />
        <div class="checkout__product-info">
          <div class="checkout__product-title">{{ product?.title }}</div>
          <div class="checkout__product-spec">{{ specName }}</div>
          <div class="checkout__product-bottom">
            <span class="checkout__product-price">¥{{ unitPrice }}</span>
            <van-stepper
                v-model="quantity"
                :min="1"
                :max="specStock"
                disable-input
                size="small"
            />
          </div>
        </div>
      </div>

      <!-- 联系人信息 -->
      <div class="checkout__card">
        <div class="checkout__card-title">联系人信息</div>
        <van-cell-group inset>
          <van-field
              v-model="contactName"
              label="姓名"
              placeholder="请输入联系人姓名"
              :rules="[{ required: true, message: '请填写联系人姓名' }]"
          />
          <van-field
              v-model="contactPhone"
              type="tel"
              label="手机号"
              placeholder="请输入手机号"
              :rules="[
              { required: true, message: '请填写手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]"
          />
          <van-field
              v-model="remark"
              label="备注"
              type="textarea"
              placeholder="选填，如特殊需求、过敏信息等"
              rows="2"
              autosize
              maxlength="200"
              show-word-limit
          />
        </van-cell-group>
      </div>

      <!-- 优惠券 & 积分 -->
      <div class="checkout__card">
        <div class="checkout__card-title">优惠抵扣</div>
        <van-cell-group inset>
          <van-cell
              title="优惠券"
              :value="selectedCoupon ? `已减 ¥${couponDiscount.toFixed(2)}` : `${usableCoupons.length}张可用`"
              is-link
              @click="couponPopupVisible = true"
          />
          <van-cell title="积分抵扣">
            <template #value>
              <div class="checkout__points">
                <span v-if="pointsDiscount > 0" class="checkout__points-discount">
                  -¥{{ pointsDiscount.toFixed(2) }}
                </span>
                <span class="checkout__points-balance">可用 {{ pointsBalance }} 积分</span>
                <van-switch v-model="usePoints" :disabled="pointsBalance === 0" size="20" />
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- 价格明细 -->
      <div class="checkout__card">
        <div class="checkout__card-title">价格明细</div>
        <div class="checkout__price-row">
          <span class="checkout__price-label">商品总价</span>
          <span class="checkout__price-value">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <div class="checkout__price-row" v-if="couponDiscount > 0">
          <span class="checkout__price-label">优惠券抵扣</span>
          <span class="checkout__price-value checkout__price-value--minus">
            -¥{{ couponDiscount.toFixed(2) }}
          </span>
        </div>
        <div class="checkout__price-row" v-if="pointsDiscount > 0">
          <span class="checkout__price-label">积分抵扣</span>
          <span class="checkout__price-value checkout__price-value--minus">
            -¥{{ pointsDiscount.toFixed(2) }}
          </span>
        </div>
        <div class="checkout__price-row checkout__price-row--total">
          <span class="checkout__price-label">实付金额</span>
          <span class="checkout__price-total">¥{{ actualPrice.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 底部支付栏 -->
      <div class="checkout__footer">
        <div class="checkout__footer-price">
          <span class="checkout__footer-label">实付：</span>
          <span class="checkout__footer-symbol">¥</span>
          <span class="checkout__footer-amount">{{ actualPrice.toFixed(2) }}</span>
        </div>
        <van-button
            type="primary"
            round
            class="checkout__footer-btn"
            :loading="submitting"
            loading-text="提交中"
            @click="handleSubmit"
        >
          确认支付
        </van-button>
      </div>
    </template>

    <!-- 优惠券选择弹窗 -->
    <van-popup
        v-model:show="couponPopupVisible"
        position="bottom"
        round
        :style="{ maxHeight: '70%' }"
    >
      <div class="coupon-popup">
        <div class="coupon-popup__header">
          <span class="coupon-popup__title">选择优惠券</span>
          <van-icon name="cross" class="coupon-popup__close" @click="couponPopupVisible = false" />
        </div>
        <div class="coupon-popup__list">
          <div
              class="coupon-popup__item"
              :class="{ 'coupon-popup__item--active': selectedCouponId === null }"
              @click="selectCoupon(null)"
          >
            <span class="coupon-popup__item-name">不使用优惠券</span>
            <van-icon v-if="selectedCouponId === null" name="success" class="coupon-popup__check" />
          </div>
          <div
              v-for="coupon in usableCoupons"
              :key="coupon.id"
              class="coupon-popup__item"
              :class="{ 'coupon-popup__item--active': selectedCouponId === coupon.id }"
              @click="selectCoupon(coupon.id)"
          >
            <div class="coupon-popup__item-left">
              <span class="coupon-popup__item-value">¥{{ coupon.discountValue }}</span>
              <span class="coupon-popup__item-condition">满{{ coupon.minAmount }}可用</span>
            </div>
            <div class="coupon-popup__item-right">
              <div class="coupon-popup__item-title">{{ coupon.couponTitle }}</div>
              <div class="coupon-popup__item-expire">{{ coupon.expireAt }}</div>
            </div>
            <van-icon v-if="selectedCouponId === coupon.id" name="success" class="coupon-popup__check" />
          </div>
          <div v-if="usableCoupons.length === 0" class="coupon-popup__empty">
            暂无可用优惠券
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, showSuccessToast } from 'vant';
import { useUserStore } from '@/stores/user';
import { getProductDetail } from '@/api/product';
import { calculateOrder, createOrder, payOrder } from '@/api/order';
import { getUsableCoupons } from '@/api/coupon';
import { getPointsBalance } from '@/api/points';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(true);
const submitting = ref(false);

// 路由参数
const productId = ref(Number(route.query.productId));
const specId = ref(Number(route.query.specId));
const quantity = ref(Number(route.query.quantity) || 1);

// 商品信息
const product = ref(null);
const specStock = ref(999);

// 表单
const contactName = ref('');
const contactPhone = ref('');
const remark = ref('');

// 优惠券
const couponPopupVisible = ref(false);
const usableCoupons = ref([]);
const selectedCouponId = ref(null);

// 积分
const pointsBalance = ref(0);
const usePoints = ref(false);

// 价格计算结果
const unitPrice = ref(0);
const totalPrice = ref(0);
const couponDiscount = ref(0);
const pointsDiscount = ref(0);
const actualPrice = ref(0);
const maxPointsDiscount = ref(0);

const productImage = computed(() => {
  if (!product.value) return '';
  return product.value.coverImage || product.value.images?.[0] || '';
});

const specName = computed(() => {
  if (!product.value?.specs) return '';
  const spec = product.value.specs.find((s) => s.id === specId.value);
  return spec ? spec.name : '';
});

const selectedCoupon = computed(() =>
    usableCoupons.value.find((c) => c.id === selectedCouponId.value)
);

const pointsUsed = computed(() => (usePoints.value ? pointsBalance.value : 0));

onMounted(async () => {
  // 登录校验
  if (!userStore.isLoggedIn) {
    showToast('请先登录');
    router.replace('/login');
    return;
  }
  if (!productId.value || !specId.value) {
    showToast('参数错误');
    router.back();
    return;
  }
  await Promise.all([fetchProduct(), fetchPointsBalance()]);
  await fetchUsableCoupons();
  await recalculate();
  loading.value = false;
});

// 数量变化 → 重新计算价格 + 刷新可用券
watch(quantity, () => {
  fetchUsableCoupons();
  recalculate();
});

// 选择优惠券 → 重新计算
function selectCoupon(id) {
  selectedCouponId.value = id;
  couponPopupVisible.value = false;
  recalculate();
}

// 积分开关变化 → 重新计算
watch(usePoints, () => {
  recalculate();
});

async function fetchProduct() {
  try {
    const res = await getProductDetail(productId.value);
    if (res.code === 0) {
      product.value = res.data;
      const spec = res.data.specs?.find((s) => s.id === specId.value);
      if (spec) {
        specStock.value = spec.stock;
        unitPrice.value = spec.price;
      }
    }
  } catch {
    showToast('商品信息加载失败');
  }
}

async function fetchPointsBalance() {
  try {
    const res = await getPointsBalance();
    if (res.code === 0) {
      pointsBalance.value = res.data.balance || 0;
    }
  } catch {
    // 积分余额获取失败不影响主流程
  }
}

async function fetchUsableCoupons() {
  try {
    // 用当前商品总价估算门槛过滤
    const estimateAmount = unitPrice.value * quantity.value;
    const res = await getUsableCoupons(estimateAmount);
    if (res.code === 0) {
      usableCoupons.value = res.data.list || [];
      // 如果当前选中的券不在可用列表中，清除选择
      if (
          selectedCouponId.value &&
          !usableCoupons.value.some((c) => c.id === selectedCouponId.value)
      ) {
        selectedCouponId.value = null;
      }
    }
  } catch {
    usableCoupons.value = [];
  }
}

async function recalculate() {
  try {
    const res = await calculateOrder({
      productId: productId.value,
      specId: specId.value,
      quantity: quantity.value,
      couponId: selectedCouponId.value
    });
    if (res.code === 0) {
      const data = res.data;
      unitPrice.value = data.unitPrice;
      totalPrice.value = data.totalPrice;
      couponDiscount.value = data.couponDiscount || 0;
      maxPointsDiscount.value = data.maxPointsDiscount || 0;
      // 积分抵扣金额取后端返回的最大可抵扣金额（已根据用户积分余额计算）
      pointsDiscount.value = usePoints.value ? data.maxPointsDiscount || 0 : 0;
      actualPrice.value = data.actualPrice - pointsDiscount.value;
      if (actualPrice.value < 0) actualPrice.value = 0;
    }
  } catch {
    // 计算失败不阻断，使用本地估算
    totalPrice.value = unitPrice.value * quantity.value;
    actualPrice.value = totalPrice.value - couponDiscount.value - pointsDiscount.value;
  }
}

async function handleSubmit() {
  // 表单校验
  if (!contactName.value.trim()) {
    showToast('请填写联系人姓名');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(contactPhone.value.trim())) {
    showToast('请填写正确的手机号');
    return;
  }

  submitting.value = true;
  try {
    // 1. 创建订单
    const createRes = await createOrder({
      productId: productId.value,
      specId: specId.value,
      quantity: quantity.value,
      couponId: selectedCouponId.value,
      pointsUsed: pointsUsed.value,
      contactName: contactName.value.trim(),
      contactPhone: contactPhone.value.trim(),
      remark: remark.value.trim()
    });

    if (createRes.code !== 0) {
      showToast(createRes.message || '下单失败');
      return;
    }

    const orderId = createRes.data.id;

    // 2. 模拟支付
    const payRes = await payOrder(orderId);
    if (payRes.code === 0) {
      showSuccessToast('支付成功');
      setTimeout(() => {
        router.replace('/order-list');
      }, 1500);
    } else {
      showToast(payRes.message || '支付失败');
      // 支付失败跳订单详情
      router.replace(`/order-list?status=pending`);
    }
  } catch (err) {
    const msg = err?.message || '提交失败，请稍后重试';
    showToast(msg);
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.checkout {
  min-height: 100vh;
  background-color: var(--color-bg, #f7f8fa);
  padding: var(--spacing-sm, 8px);
  padding-bottom: 70px;
}

.checkout__loading {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.checkout__card {
  background-color: var(--color-white, #fff);
  border-radius: var(--border-radius, 8px);
  padding: var(--spacing-lg, 16px);
  margin-bottom: var(--spacing-sm, 8px);
}

.checkout__card-title {
  font-size: var(--font-size-md, 14px);
  font-weight: 600;
  color: var(--color-text, #323233);
  margin-bottom: var(--spacing-md, 12px);
}

/* 商品快照 */
.checkout__product-img {
  float: left;
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius, 8px);
  object-fit: cover;
  margin-right: var(--spacing-md, 12px);
}

.checkout__product-info {
  overflow: hidden;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.checkout__product-title {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
  font-weight: 500;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.checkout__product-spec {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-text-light, #969799);
  margin-top: var(--spacing-xs, 4px);
}

.checkout__product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-xs, 4px);
}

.checkout__product-price {
  font-size: var(--font-size-lg, 16px);
  color: var(--color-danger, #ee0a24);
  font-weight: bold;
}

/* 积分行 */
.checkout__points {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 8px);
}

.checkout__points-discount {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-danger, #ee0a24);
}

.checkout__points-balance {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-text-light, #969799);
}

/* 价格明细 */
.checkout__price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs, 4px) 0;
  font-size: var(--font-size-md, 14px);
}

.checkout__price-label {
  color: var(--color-text-light, #969799);
}

.checkout__price-value {
  color: var(--color-text, #323233);
}

.checkout__price-value--minus {
  color: var(--color-danger, #ee0a24);
}

.checkout__price-row--total {
  margin-top: var(--spacing-sm, 8px);
  padding-top: var(--spacing-md, 12px);
  border-top: 1px solid #ebedf0;
}

.checkout__price-total {
  font-size: 20px;
  font-weight: bold;
  color: var(--color-danger, #ee0a24);
}

/* 底部支付栏 */
.checkout__footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: var(--color-white, #fff);
  padding: var(--spacing-sm, 8px) var(--spacing-lg, 16px);
  padding-bottom: calc(var(--spacing-sm, 8px) + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.checkout__footer-price {
  display: flex;
  align-items: baseline;
}

.checkout__footer-label {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
}

.checkout__footer-symbol {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-danger, #ee0a24);
  font-weight: bold;
}

.checkout__footer-amount {
  font-size: 24px;
  color: var(--color-danger, #ee0a24);
  font-weight: bold;
  margin-left: 1px;
}

.checkout__footer-btn {
  width: 130px;
  height: 42px;
}

/* 优惠券弹窗 */
.coupon-popup {
  display: flex;
  flex-direction: column;
  max-height: 70vh;
}

.coupon-popup__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg, 16px);
  border-bottom: 1px solid #ebedf0;
}

.coupon-popup__title {
  font-size: var(--font-size-lg, 16px);
  font-weight: 600;
  color: var(--color-text, #323233);
}

.coupon-popup__close {
  font-size: 20px;
  color: var(--color-text-light, #969799);
  cursor: pointer;
}

.coupon-popup__list {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-sm, 8px) var(--spacing-lg, 16px);
}

.coupon-popup__item {
  position: relative;
  display: flex;
  align-items: center;
  padding: var(--spacing-md, 12px);
  margin-bottom: var(--spacing-sm, 8px);
  border: 1px solid #ebedf0;
  border-radius: var(--border-radius, 8px);
  cursor: pointer;
}

.coupon-popup__item--active {
  border-color: var(--color-primary, #1989fa);
  background-color: rgba(25, 137, 250, 0.05);
}

.coupon-popup__item-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80px;
  padding-right: var(--spacing-md, 12px);
  border-right: 1px dashed #ebedf0;
}

.coupon-popup__item-value {
  font-size: 22px;
  font-weight: bold;
  color: var(--color-danger, #ee0a24);
}

.coupon-popup__item-condition {
  font-size: 10px;
  color: var(--color-text-light, #969799);
  margin-top: 2px;
}

.coupon-popup__item-right {
  flex: 1;
  padding-left: var(--spacing-md, 12px);
}

.coupon-popup__item-title {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
  font-weight: 500;
}

.coupon-popup__item-expire {
  font-size: var(--font-size-xs, 10px);
  color: var(--color-text-light, #969799);
  margin-top: var(--spacing-xs, 4px);
}

.coupon-popup__item-name {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
}

.coupon-popup__check {
  position: absolute;
  right: var(--spacing-md, 12px);
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-primary, #1989fa);
  font-size: 18px;
}

.coupon-popup__empty {
  text-align: center;
  padding: 40px 0;
  color: var(--color-text-light, #969799);
  font-size: var(--font-size-md, 14px);
}
</style>
