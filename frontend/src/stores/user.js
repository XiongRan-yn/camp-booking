import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useUserStore = defineStore("user", () => {
  const token = ref(localStorage.getItem("token") || "");
  const userInfo = ref(null);

  const isLoggedIn = computed(() => !!token.value);

  function login(data) {
    token.value = data.token;
    userInfo.value = data.user;
    localStorage.setItem("token", data.token);
  }

  function setUser(info) {
    userInfo.value = info;
  }

  function logout() {
    token.value = "";
    userInfo.value = null;
    localStorage.removeItem("token");
  }

  return { token, userInfo, isLoggedIn, login, setUser, logout };
});
