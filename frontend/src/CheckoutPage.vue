<template>
  <div class="checkout-page">
    <!--商品快照-->
    <van-cell-group inset>
      <van-cell title="商品">
        <template #value>
          <div class="product-snapshot">
            <img :src="query.productImage" alt="" class="snap-img" />
            <div class="snap-info">
              <div>{{ query.productTitle }}</div>
              <div class="snap-spec">{{ query.specName }} × {{ form.quantity }}</div>
            </div>
          </div>
        </template>
      </van-cell>
    </van-cell-group>

    <!--联系人表单-->
    <van-cell-group inset>
      <van-field
        v-model="form.contactName"
        label="联系人"
        placeholder="请填写联系人姓名"
      />
      <van-field
        v-model="form.contactPhone"
        label="联系手机"
        placeholder="请填写手机号"
      />
      <van-field
        v-model="form.remark"
        label="备注"
        type="textarea"
        rows="2"
        placeholder="选填，填写特殊需求"
      />
    </van-cell-group>

    <!--优惠券选择-->
    <van-cell-group inset>
      <van-cell
        title="优惠券"
        :value="selectedCoupon ? selectedCoupon.title : '选择优惠券'"
        is-link
        @click="showCouponPopup = true"
      />
      <van-cell title="积分抵扣" :value="`可用${balance}积分`">
        <template #right-icon>
          <van-stepper
            v-model="form.pointsUsed"
            :min="0"
            :max="maxPointsDiscount"
            @change="recalcPrice"
          />
        </template>
      </van-cell>
    </van-cell-group>

    <!--价格汇总-->
    <van-cell-group inset>
      <van-cell title="商品总价" :value="`¥${priceInfo.totalPrice}`" />
      <van-cell title="优惠券减免" :value="`-¥${priceInfo.couponDiscount}`" />
      <van-cell title="积分抵扣" :value="`-¥${priceInfo.pointsDiscount}`" />
    </van-cell-group>

    <!--底部结算栏-->
    <div class="checkout-footer">
      <div class="footer-price">
        <span>实付：</span>
        <span class="real-pay">¥{{ priceInfo.actualPrice }}</span>
      </div>
      <van-button type="primary" @click="submitOrder">提交订单</van-button>
    </div>

    <!--优惠券弹窗-->
    <van-popup v-model:show="showCouponPopup" position="bottom" round>
      <div class="coupon-popup">
        <div class="popup-title">选择可用优惠券</div>
        <van-radio-group v-model="form.couponId">
          <van-cell-group>
            <van-cell title="不使用优惠券" clickable>
              <template #title>
                <van-radio :name="null">不使用优惠券</van-radio>
              </template>
            </van-cell>
            <van-cell
              v-for="c in availableCoupons"
              :key="c.id"
              clickable
            >
              <template #title>
                <van-radio :name="c.id">{{ c.couponTitle }} 减{{c.discountValue}}</van-radio>
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>
        <van-button block type="primary" @click="couponConfirm">确定选择</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const query = route.query

const form = ref({
  productId: Number(query.productId),
  specId: Number(query.specId),
  quantity: Number(query.quantity),
  couponId: null,
  pointsUsed: 0,
  contactName: '',
  contactPhone: '',
  remark: ''
})

// 价格计算结果
const priceInfo = ref({
  unitPrice: 0,
  totalPrice: 0,
  couponDiscount:0,
  pointsDiscount:0,
  actualPrice:0
})
const availableCoupons = ref([])
const balance = ref(0)
const maxPointsDiscount = ref(0)
const showCouponPopup = ref(false)
const selectedCoupon = ref(null)

// 获取积分余额
async function getPointsBalance() {
  const res = await request.get('/points/balance')
  balance.value = res.data.balance
}

// 核心：调用计算价格接口
async function recalcPrice() {
  try {
    const body = {
      productId: form.value.productId,
      specId: form.value.specId,
      quantity: form.value.quantity,
      couponId: form.value.couponId
    }
    const res = await request.post('/orders/calculate', body)
    priceInfo.value = res.data
    availableCoupons.value = res.data.availableCoupons
    maxPointsDiscount.value = res.data.maxPointsDiscount
  } catch(err) {
    showToast(err?.message || '价格计算失败')
  }
}

// 优惠券弹窗确认
function couponConfirm() {
  showCouponPopup.value = false
  // 根据couponId找到选中券
  if(form.value.couponId) {
    selectedCoupon.value = availableCoupons.value.find(i=>i.id === form.value.couponId)
  } else {
    selectedCoupon.value = null
  }
  recalcPrice()
}

// 提交订单
async function submitOrder() {
  if(!form.value.contactName) return showToast('请填写联系人姓名')
  if(!form.value.contactPhone) return showToast('请填写手机号')
  try {
    const res = await request.post('/orders', form.value)
    showToast('订单创建成功')
    // 创建订单成功后跳转订单详情页
    router.push(`/order-list`)
  } catch(e) {
    showToast(e?.message || '提交订单失败')
  }
}

// 监听couponId变化，重新计算
watch(()=>form.value.couponId, ()=>{
  recalcPrice()
})

onMounted(async ()=>{
  if(!userStore.isLoggedIn) {
    showToast('请登录')
    router.push('/login')
    return
  }
  await getPointsBalance()
  await recalcPrice()
})
</script>

<style scoped>
.checkout-page {
  padding-bottom: 70px;
  background: var(--color-bg);
}
.product-snapshot {
  display:flex;
  gap: var(--spacing-sm);
}
.snap-img {
  width:60px;
  height:60px;
  object-fit:cover;
  border-radius: var(--border-radius);
}
.snap-info {
  font-size: var(--font-size-sm);
}
.snap-spec {
  color: var(--color-text-light);
  margin-top: 4px;
}
.coupon-popup {
  padding: var(--spacing-lg);
  max-height: 60vh;
  overflow-y:auto;
}
.popup-title {
  font-size: var(--font-size-lg);
  font-weight: 500;
  margin-bottom: var(--spacing-md);
}
.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right:0;
  height:64px;
  background:#fff;
  display:flex;
  align-items:center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  box-shadow:0 -2px 8px rgba(0,0,0,0.06);
}
.real-pay {
  color: var(--color-danger);
  font-size: 20px;
  font-weight: bold;
}
</style>