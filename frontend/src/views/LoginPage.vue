<template>
  <div class="login-page">
    <div class="login-hero">
      <div class="login-logo">营地预订</div>
      <div class="login-slogan">研学营地 · 民宿预订平台</div>
    </div>

    <div class="login-card">
      <van-tabs v-model:active="activeTab" color="#1989fa">
        <van-tab title="登录" name="login" />
        <van-tab title="注册" name="register" />
      </van-tabs>

      <div class="form-wrap">
        <van-field
          v-model="form.username"
          label="用户名"
          placeholder="请输入用户名"
          clearable
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-if="activeTab === 'register'"
          v-model="form.nickname"
          label="昵称"
          placeholder="请输入昵称"
          clearable
        />
        <van-field
          v-model="form.password"
          type="password"
          label="密码"
          placeholder="请输入密码"
          clearable
          :rules="[{ required: true, message: '请输入密码' }]"
        />

        <div class="btn-wrap">
          <van-button type="primary" block round :loading="submitting" @click="handleSubmit">
            {{ activeTab === "login" ? "登 录" : "注 册" }}
          </van-button>
        </div>

        <div class="hint">
          {{ activeTab === "login" ? "还没有账号？" : "已有账号？" }}
          <span class="link" @click="switchTab">{{ activeTab === "login" ? "去注册" : "去登录" }}</span>
        </div>
      </div>
    </div>

    <div class="login-footer">测试账号：test / 123456（或自行注册）</div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { showToast } from "vant";
import { loginApi, registerApi } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeTab = ref("login");
const submitting = ref(false);
const form = ref({ username: "", password: "", nickname: "" });

// 登录成功后，回到用户原本要访问的页面（无则回“我的”）
function goAfterLogin() {
  const redirect = route.query.redirect;
  router.replace(typeof redirect === "string" && redirect.startsWith("/") ? redirect : "/mine");
}

function switchTab() {
  activeTab.value = activeTab.value === "login" ? "register" : "login";
  form.value.password = "";
}

async function handleSubmit() {
  const username = form.value.username.trim();
  const password = form.value.password.trim();
  if (!username || !password) {
    showToast("请输入用户名和密码");
    return;
  }
  if (activeTab.value === "register" && !form.value.nickname.trim()) {
    showToast("请填写昵称");
    return;
  }
  submitting.value = true;
  try {
    if (activeTab.value === "login") {
      const res = await loginApi({ username, password });
      if (res.code === 0) {
        userStore.login(res.data);
        showToast("登录成功");
        goAfterLogin();
      } else {
        showToast(res.message || "登录失败");
      }
    } else {
      const res = await registerApi({
        username,
        password,
        nickname: form.value.nickname.trim(),
      });
      if (res.code === 0) {
        showToast("注册成功，正在登录…");
        const loginRes = await loginApi({ username, password });
        if (loginRes.code === 0) {
          userStore.login(loginRes.data);
          goAfterLogin();
        } else {
          showToast(loginRes.message || "自动登录失败");
          activeTab.value = "login";
        }
      } else {
        showToast(res.message || "注册失败");
      }
    }
  } catch (e) {
    showToast((e && e.message) || "操作失败，请稍后再试");
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 40px;
}
.login-hero {
  padding: 48px 0 32px;
  text-align: center;
  background: linear-gradient(135deg, #1989fa, #5babfb);
  color: #fff;
}
.login-logo {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
}
.login-slogan {
  margin-top: 10px;
  font-size: 14px;
  opacity: 0.9;
}
.login-card {
  margin: -16px 16px 0;
  background: #fff;
  border-radius: 12px;
  padding: 8px 4px 20px;
  position: relative;
}
.form-wrap {
  padding: 12px 12px 0;
}
.btn-wrap {
  padding: 20px 8px 4px;
}
.hint {
  text-align: center;
  font-size: 13px;
  color: #969799;
  margin-top: 14px;
}
.link {
  color: #1989fa;
}
.login-footer {
  text-align: center;
  font-size: 12px;
  color: #c8c9cc;
  margin-top: 24px;
}
</style>