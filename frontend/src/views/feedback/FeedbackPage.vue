<template>
  <div class="page-container feedback-page">
    <van-nav-bar title="反馈投诉" left-arrow fixed placeholder @click-left="goBack" />

    <div class="feedback-form">
      <van-field
        v-model="form.content"
        rows="6"
        type="textarea"
        maxlength="500"
        show-word-limit
        placeholder="请描述你遇到的问题或建议（1-500字）"
        class="feedback-form__textarea"
      />
      <van-field
        v-model="form.email"
        type="text"
        placeholder="联系邮箱（选填）"
        class="feedback-form__email"
      />
      <van-button
        block
        round
        type="primary"
        :loading="submitting"
        @click="handleSubmit"
      >
        提交反馈
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import request from "@/api/request";
import "vant/lib/index.css";

const router = useRouter();
const submitting = ref(false);
const form = reactive({ content: "", email: "" });

function goBack() {
  router.back();
}

async function handleSubmit() {
  const content = form.content.trim();
  if (!content) {
    showToast("请填写反馈内容");
    return;
  }
  if (content.length > 500) {
    showToast("反馈内容不能超过500字");
    return;
  }
  submitting.value = true;
  try {
    const res = await request.post("/feedback", {
      content,
      email: form.email.trim(),
    });
    if (res.code === 0) {
      showToast("反馈已提交，感谢你的建议");
      form.content = "";
      form.email = "";
    } else {
      showToast(res.message || "提交失败，请稍后重试");
    }
  } catch (error) {
    showToast(error.message || "提交失败，请稍后重试");
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.feedback-page {
  padding-bottom: 40px;
}

.feedback-form {
  margin: var(--spacing-md);
}

.feedback-form__textarea {
  border-radius: var(--border-radius);
  padding: var(--spacing-md);
}

.feedback-form__email {
  margin: var(--spacing-md) 0 var(--spacing-xl);
  border-radius: var(--border-radius);
}
</style>