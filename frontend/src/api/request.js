import axios from "axios";
import { useUserStore } from "@/stores/user";
import router from "@/router";

const request = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// 请求拦截：自动带 Token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 响应拦截：统一错误处理
request.interceptors.response.use(
  (res) => res.data,
  (error) => {
    const status = error.response?.status;
    // 未登录 / 登录过期（401、403）→ 清空登录态并跳转登录页
    // 登录、注册接口本身除外，避免登录失败时被反复重定向
    if ((status === 401 || status === 403) && !isAuthUrl(error.config?.url)) {
      const userStore = useUserStore();
      userStore.logout();
      const current = router.currentRoute.value;
      router.push({
        path: "/login",
        query: current.path && current.path !== "/login" ? { redirect: current.fullPath } : {},
      });
    }
    return Promise.reject(error.response?.data || error);
  },
);

function isAuthUrl(url = "") {
  return url.includes("/auth/login") || url.includes("/auth/register");
}

export default request;