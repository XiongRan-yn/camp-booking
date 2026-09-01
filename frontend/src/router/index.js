import { createRouter, createWebHistory } from 'vue-router'
import OrderList from '../views/OrderList.vue'
import CouponCenter from '../views/coupon-center.vue'
import CouponWallet from '../views/coupon-wallet.vue'
import PointsShop from '../views/points-shop.vue'
import ExchangeRecord from '../views/exchange-record.vue'

const routes = [
  {
    path: '/orderList',
    name: 'OrderList',
    component: OrderList
  },
  {
    path: '/couponCenter',
    name: 'CouponCenter',
    component: CouponCenter
  },
  {
    path: '/couponWallet',
    name: 'CouponWallet',
    component: CouponWallet
  },
  {
    path: '/pointsShop',
    name: 'PointsShop',
    component: PointsShop
  },
  {
    path: '/exchangeRecord',
    name: 'ExchangeRecord',
    component: ExchangeRecord
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
export default router