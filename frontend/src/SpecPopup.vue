<template>
  <van-popup
    v-model:show="visible"
    position="bottom"
    round
  >
    <div class="spec-popup">
      <!-- 商品简要信息 -->
      <div class="spec-popup__header">
        <img class="spec-popup__cover" :src="productInfo.coverImage" alt="">
        <div class="spec-popup__meta">
          <div class="spec-popup__title">{{ productInfo.title }}</div>
          <div class="spec-popup__price">
            <span class="price-symbol">¥</span>
            <span class="price-num">{{ selectedSpec?.price || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="spec-popup__body">
        <div class="spec-group-title">选择规格</div>
        <van-radio-group v-model="selectedSpecId">
          <van-cell-group>
            <van-cell
              v-for="item in productInfo.specs"
              :key="item.id"
              clickable
            >
              <template #title>
                <van-radio :name="item.id">
                  {{ item.name }}
                  <span class="spec-price">¥{{ item.price }}</span>
                  <span v-if="item.stock <=0" class="stock-tip">已售罄</span>
                </van-radio>
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>

        <!-- 购买数量 -->
        <div class="spec-quantity">
          <span class="qty-label">购买数量</span>
          <van-stepper
            v-model="quantity"
            :min="1"
            :max="selectedSpec?.stock ?? 1"
          />
        </div>
      </div>

      <div class="spec-popup__footer">
        <van-button
          type="primary"
          block
          @click="handleConfirm"
          :disabled="!selectedSpecId"
        >
          确定
        </van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { computed, watch } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  productInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:show', 'confirm'])

const visible = computed({
  get() { return props.show },
  set(v) { emit('update:show', v) }
})

const selectedSpecId = ref(null)
const quantity = ref(1)

// 当前选中规格
const selectedSpec = computed(() => {
  if (!selectedSpecId.value || !props.productInfo.specs) return null
  return props.productInfo.specs.find(s => s.id === selectedSpecId.value)
})

// 弹窗打开重置状态
watch(visible, (val) => {
  if(val) {
    selectedSpecId.value = null
    quantity.value = 1
  }
})

function handleConfirm() {
  if(!selectedSpec.value) return
  emit('confirm', {
    specId: selectedSpec.value.id,
    specName: selectedSpec.value.name,
    price: selectedSpec.value.price,
    quantity: quantity.value
  })
  visible.value = false
}
</script>

<style scoped>
.spec-popup {
  padding: var(--spacing-lg);
  max-height: 70vh;
  overflow-y: auto;
}
.spec-popup__header {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}
.spec-popup__cover {
  width: 80px;
  height:80px;
  object-fit: cover;
  border-radius: var(--border-radius);
}
.spec-popup__title {
  font-size: var(--font-size-md);
  color: var(--color-text);
}
.spec-popup__price {
  color: var(--color-danger);
  margin-top: var(--spacing-xs);
}
.price-num {
  font-size: 22px;
  font-weight: bold;
}
.spec-group-title {
  font-size: var(--font-size-md);
  font-weight: 500;
  margin: var(--spacing-md) 0;
}
.spec-price {
  color: var(--color-danger);
  margin-left: 8px;
}
.stock-tip {
  color: var(--color-text-light);
  margin-left:8px;
}
.spec-quantity {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: var(--spacing-lg) 0;
}
.qty-label {
  font-size: var(--font-size-md);
}
.spec-popup__footer {
  margin-top: var(--spacing-lg);
}
</style>