<template>
  <van-popup
      v-model:show="popupVisible"
      position="bottom"
      round
      :style="{ maxHeight: '80%' }"
      @update:show="handleVisibleChange"
  >
    <div class="spec-popup">
      <!-- 商品信息头部 -->
      <div class="spec-popup__header">
        <img
            class="spec-popup__cover"
            :src="product.coverImage || product.images?.[0] || placeholderImg"
            alt="商品封面"
        />
        <div class="spec-popup__info">
          <div class="spec-popup__price">
            <span class="spec-popup__price-symbol">¥</span>
            <span class="spec-popup__price-value">{{ currentPrice }}</span>
            <span
                v-if="currentSpec?.originalPrice"
                class="spec-popup__price-origin"
            >
              ¥{{ currentSpec.originalPrice }}
            </span>
          </div>
          <div class="spec-popup__stock">
            库存 {{ currentSpec ? currentSpec.stock : 0 }} 件
          </div>
          <div class="spec-popup__selected">
            已选：{{ currentSpec ? currentSpec.name : '请选择规格' }}
          </div>
        </div>
        <van-icon name="cross" class="spec-popup__close" @click="close" />
      </div>

      <!-- 规格列表 -->
      <div class="spec-popup__body">
        <div class="spec-popup__section-title">选择规格</div>
        <div class="spec-popup__spec-list">
          <div
              v-for="spec in product.specs"
              :key="spec.id"
              class="spec-popup__spec-item"
              :class="{
              'spec-popup__spec-item--active': spec.id === selectedSpecId,
              'spec-popup__spec-item--disabled': spec.stock <= 0
            }"
              @click="handleSelectSpec(spec)"
          >
            <div class="spec-popup__spec-name">{{ spec.name }}</div>
            <div class="spec-popup__spec-price">¥{{ spec.price }}</div>
          </div>
        </div>

        <!-- 数量选择 -->
        <div class="spec-popup__quantity">
          <span class="spec-popup__quantity-label">购买数量</span>
          <van-stepper
              v-model="localQuantity"
              :min="1"
              :max="currentSpec ? currentSpec.stock : 1"
              disable-input
          />
        </div>
      </div>

      <!-- 底部确定按钮 -->
      <div class="spec-popup__footer">
        <van-button
            type="primary"
            block
            round
            :disabled="!currentSpec || currentSpec.stock <= 0"
            @click="handleConfirm"
        >
          确定
        </van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  product: {
    type: Object,
    default: () => ({ specs: [] })
  },
  selectedSpecId: {
    type: [Number, String],
    default: null
  },
  quantity: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits([
  'update:visible',
  'update:selectedSpecId',
  'update:quantity',
  'select-spec',
  'confirm'
]);

const placeholderImg =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect fill="%23f2f3f5" width="200" height="200"/><text x="50%25" y="50%25" font-size="14" fill="%23969799" text-anchor="middle" dy=".3em">暂无图片</text></svg>';

const popupVisible = ref(props.visible);
const localQuantity = ref(props.quantity);

const currentSpec = computed(() => {
  if (!props.product?.specs) return null;
  return props.product.specs.find((s) => s.id === props.selectedSpecId) || null;
});

const currentPrice = computed(() => {
  return currentSpec.value ? currentSpec.value.price : props.product.minPrice || 0;
});

watch(
    () => props.visible,
    (val) => {
      popupVisible.value = val;
    }
);

watch(
    () => props.quantity,
    (val) => {
      localQuantity.value = val;
    }
);

watch(localQuantity, (val) => {
  emit('update:quantity', val);
});

function handleVisibleChange(val) {
  emit('update:visible', val);
}

function handleSelectSpec(spec) {
  if (spec.stock <= 0) return;
  emit('update:selectedSpecId', spec.id);
  emit('select-spec', spec);
}

function handleConfirm() {
  if (!currentSpec.value) return;
  emit('confirm', {
    spec: currentSpec.value,
    quantity: localQuantity.value
  });
  close();
}

function close() {
  popupVisible.value = false;
  emit('update:visible', false);
}
</script>

<style scoped>
.spec-popup {
  display: flex;
  flex-direction: column;
  max-height: 80vh;
}

.spec-popup__header {
  position: relative;
  display: flex;
  padding: var(--spacing-lg, 16px);
  border-bottom: 1px solid #ebedf0;
}

.spec-popup__cover {
  width: 96px;
  height: 96px;
  border-radius: var(--border-radius, 8px);
  object-fit: cover;
  flex-shrink: 0;
}

.spec-popup__info {
  flex: 1;
  margin-left: var(--spacing-md, 12px);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.spec-popup__price {
  display: flex;
  align-items: baseline;
  color: var(--color-danger, #ee0a24);
}

.spec-popup__price-symbol {
  font-size: var(--font-size-md, 14px);
  font-weight: bold;
}

.spec-popup__price-value {
  font-size: 24px;
  font-weight: bold;
  margin-left: 2px;
}

.spec-popup__price-origin {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-text-light, #969799);
  text-decoration: line-through;
  margin-left: var(--spacing-sm, 8px);
}

.spec-popup__stock {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-text-light, #969799);
  margin-top: var(--spacing-xs, 4px);
}

.spec-popup__selected {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-text, #323233);
  margin-top: var(--spacing-xs, 4px);
}

.spec-popup__close {
  position: absolute;
  top: var(--spacing-md, 12px);
  right: var(--spacing-md, 12px);
  font-size: 20px;
  color: var(--color-text-light, #969799);
  cursor: pointer;
}

.spec-popup__body {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg, 16px);
}

.spec-popup__section-title {
  font-size: var(--font-size-md, 14px);
  font-weight: 600;
  color: var(--color-text, #323233);
  margin-bottom: var(--spacing-md, 12px);
}

.spec-popup__spec-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm, 8px);
}

.spec-popup__spec-item {
  min-width: 100px;
  padding: var(--spacing-sm, 8px) var(--spacing-md, 12px);
  border: 1px solid #ebedf0;
  border-radius: var(--border-radius, 8px);
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.spec-popup__spec-item--active {
  border-color: var(--color-primary, #1989fa);
  background-color: rgba(25, 137, 250, 0.08);
}

.spec-popup__spec-item--disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.spec-popup__spec-name {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
}

.spec-popup__spec-item--active .spec-popup__spec-name {
  color: var(--color-primary, #1989fa);
  font-weight: 600;
}

.spec-popup__spec-price {
  font-size: var(--font-size-sm, 12px);
  color: var(--color-danger, #ee0a24);
  margin-top: 2px;
}

.spec-popup__quantity {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-xl, 24px);
  padding-top: var(--spacing-lg, 16px);
  border-top: 1px solid #ebedf0;
}

.spec-popup__quantity-label {
  font-size: var(--font-size-md, 14px);
  color: var(--color-text, #323233);
}

.spec-popup__footer {
  padding: var(--spacing-md, 12px) var(--spacing-lg, 16px);
  padding-bottom: calc(var(--spacing-md, 12px) + env(safe-area-inset-bottom));
  border-top: 1px solid #ebedf0;
}
</style>
