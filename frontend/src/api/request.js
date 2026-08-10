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
    if (error.response?.status === 401) {
      const userStore = useUserStore();
      userStore.logout();
      router.push("/mine");
    }
    return Promise.reject(error.response?.data || error);
  },
);

export default request;
