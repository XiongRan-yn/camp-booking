import { createRouter, createWebHistory } from "vue-router";

const routes = [
  { path: "/", redirect: "/booking" },
  {
    path: "/home",
    name: "Home",
    component: () => import("@/views/home/HomePage.vue"),
  },
  {
    path: "/booking",
    name: "Booking",
    component: () => import("@/views/booking/BookingPage.vue"),
  },
  {
    path: "/mine",
    name: "Mine",
    component: () => import("@/views/mine/MinePage.vue"),
  },
  {
    path: "/product/:id",
    name: "ProductDetail",
    component: () => import("@/views/product-detail/ProductDetail.vue"),
  },
  {
    path: "/checkout",
    name: "Checkout",
    component: () => import("@/views/checkout/CheckoutPage.vue"),
  },
  {
    path: "/order-list",
    name: "OrderList",
    component: () => import("@/views/order-list/OrderList.vue"),
  },
  {
    path: "/favorites",
    name: "Favorites",
    component: () => import("@/views/favorites/FavoritesPage.vue"),
  },
  {
    path: "/coupon-center",
    name: "CouponCenter",
    component: () => import("@/views/coupon-center/CouponCenter.vue"),
  },
  {
    path: "/coupon-wallet",
    name: "CouponWallet",
    component: () => import("@/views/coupon-wallet/CouponWallet.vue"),
  },
  {
    path: "/points-mall",
    name: "PointsMall",
    component: () => import("@/views/points-mall/PointsMall.vue"),
  },
  {
    path: "/traveler-info",
    name: "TravelerInfo",
    component: () => import("@/views/traveler-info/TravelerInfo.vue"),
  },
  {
    path: "/redeem",
    name: "Redeem",
    component: () => import("@/views/redeem/RedeemPage.vue"),
  },
  {
    path: "/reviews",
    name: "Reviews",
    component: () => import("@/views/reviews/ReviewsPage.vue"),
  },
  {
    path: "/settings",
    name: "Settings",
    component: () => import("@/views/settings/SettingsPage.vue"),
  },
  {
    path: "/feedback",
    name: "Feedback",
    component: () => import("@/views/feedback/FeedbackPage.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
