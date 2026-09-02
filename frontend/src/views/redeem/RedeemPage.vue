<template>
  <div class="redeem-page">
    <van-nav-bar title="兑换中心" fixed placeholder />

    <div class="redeem-card">
      <van-field v-model="code" label="兑换码" placeholder="请输入兑换码" clearable maxlength="30" />
      <div class="btn-wrap">
        <van-button type="primary" block round :disabled="!code" :loading="submitting" @click="handleSubmit">
          立即兑换
        </van-button>
      </div>
      <div class="redeem-tip">
        <p>兑换说明：</p>
        <p>1. 输入兑换码可兑换优惠券、免房券或积分；</p>
        <p>2. 兑换成功后可在「卡券包」或「积分明细」中查看；</p>
        <p>3. 每个兑换码仅可使用一次。</p>
      </div>
    </div>

    <div v-if="result" class="result-card">
      <div class="result-title">{{ result.success ? "兑换成功" : "兑换失败" }}</div>
      <div class="result-msg">{{ result.message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import { submitRedeemCode } from "@/api/redeem";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const code = ref("");
const submitting = ref(false);
const result = ref(null);

async function handleSubmit() {
  const input = code.value.trim();
  if (!input) {
    showToast("请输入兑换码");
    return;
  }
  if (!userStore.isLoggedIn) {
    showToast("请先登录");
    router.push({ path: "/login", query: { redirect: "/redeem" } });
    return;
  }
  submitting.value = true;
  result.value = null;
  try {
    const res = await submitRedeemCode(input);
    if (res.code === 0) {
      const data = res.data || {};
      const message = data.couponName ? `获得「${data.couponName}」，有效期至 ${String(data.expireAt || "").slice(0, 10)}` : data.message || "兑换成功";
      result.value = { success: true, message };
      showToast("兑换成功");
      code.value = "";
    } else {
      result.value = { success: false, message: res.message || "兑换失败" };
      showToast(res.message || "兑换失败");
    }
  } catch (e) {
    const msg = (e && e.message) || "兑换失败，请检查兑换码";
    result.value = { success: false, message: msg };
    showToast(msg);
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.redeem-card {
  background: #fff;
  margin: 12px;
  border-radius: 10px;
  padding: 20px 16px;
}
.btn-wrap {
  padding: 16px 4px 0;
}
.redeem-tip {
  margin-top: 16px;
  font-size: 12px;
  color: #969799;
  line-height: 1.8;
}
.redeem-tip p {
  margin: 0;
}
.result-card {
  background: #fff;
  margin: 12px;
  border-radius: 10px;
  padding: 16px;
  text-align: center;
}
.result-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}
.result-msg {
  margin-top: 8px;
  font-size: 13px;
  color: #646566;
}
</style>