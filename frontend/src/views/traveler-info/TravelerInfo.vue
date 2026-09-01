<template>
  <div class="page-container traveler-page">
    <van-nav-bar title="出行人信息" left-arrow fixed placeholder @click-left="goBack">
      <template #right>
        <van-icon name="plus" size="18" @click="openCreate" />
      </template>
    </van-nav-bar>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="list.length" class="traveler-list">
        <div v-for="item in list" :key="item.id" class="traveler-card">
          <div class="traveler-card__header">
            <span class="traveler-card__name">{{ item.name }}</span>
            <van-tag :type="item.gender === 'female' ? 'danger' : 'primary'">
              {{ item.gender === 'female' ? '女' : '男' }}
            </van-tag>
            <span class="traveler-card__age">{{ item.age }}岁</span>
          </div>
          <div class="traveler-card__row">手机：{{ item.phone || "--" }}</div>
          <div class="traveler-card__row">证件：{{ maskIdCard(item.idCard) }}</div>
          <div class="traveler-card__actions">
            <van-button size="small" plain type="primary" @click="openEdit(item)">编辑</van-button>
            <van-button size="small" plain type="danger" @click="handleDelete(item)">删除</van-button>
          </div>
        </div>
      </div>
      <van-empty v-else-if="!loading" description="还没有出行人，点右上角添加" />
    </van-pull-refresh>

    <!-- 新增/编辑弹窗 -->
    <van-popup v-model:show="showPopup" position="bottom" round :style="{ height: '70%' }">
      <div class="traveler-form">
        <div class="traveler-form__title">{{ editingId ? "编辑出行人" : "新增出行人" }}</div>
        <van-field v-model="form.name" label="姓名" placeholder="请输入姓名" />
        <van-field v-model="form.phone" label="手机号" type="tel" placeholder="请输入手机号" />
        <van-field v-model="form.idCard" label="身份证" placeholder="请输入身份证号" />
        <van-field v-model="form.age" label="年龄" type="digit" placeholder="请输入年龄" />
        <van-field name="gender" label="性别">
          <template #input>
            <van-radio-group v-model="form.gender" direction="horizontal">
              <van-radio name="male">男</van-radio>
              <van-radio name="female">女</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <div class="traveler-form__actions">
          <van-button round block type="primary" :loading="submitting" @click="handleSave">
            保存
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { showToast, showConfirmDialog } from "vant";
import request from "@/api/request";
import "vant/lib/index.css";

const router = useRouter();
const emptyForm = { name: "", phone: "", idCard: "", age: "", gender: "male" };

const list = ref([]);
const loading = ref(false);
const refreshing = ref(false);
const showPopup = ref(false);
const submitting = ref(false);
const editingId = ref(null);
const form = reactive({ ...emptyForm });

function goBack() {
  router.back();
}

function maskIdCard(value) {
  if (!value || value.length < 8) return value || "--";
  return value.slice(0, 4) + "**********" + value.slice(-4);
}

async function fetchTravelers() {
  loading.value = true;
  try {
    const res = await request.get("/travelers");
    if (res.code === 0) {
      const data = res.data;
      list.value = Array.isArray(data) ? data : data?.list || [];
    } else {
      showToast(res.message || "加载失败");
    }
  } catch (error) {
    showToast(error.message || "加载失败");
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
}

function onRefresh() {
  fetchTravelers();
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, emptyForm);
  showPopup.value = true;
}

function openEdit(item) {
  editingId.value = item.id;
  Object.assign(form, {
    id: item.id,
    name: item.name || "",
    phone: item.phone || "",
    idCard: item.idCard || "",
    age: item.age ?? "",
    gender: item.gender || "male",
  });
  showPopup.value = true;
}

function validate() {
  if (!form.name.trim()) {
    showToast("请输入姓名");
    return false;
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    showToast("请输入正确的手机号");
    return false;
  }
  if (!form.idCard.trim()) {
    showToast("请输入身份证号");
    return false;
  }
  const age = Number(form.age);
  if (!age || age < 0 || age > 120) {
    showToast("请输入正确的年龄");
    return false;
  }
  return true;
}

async function handleSave() {
  if (!validate()) return;
  submitting.value = true;
  const payload = { ...form, age: Number(form.age) };
  try {
    const res = editingId.value
      ? await request.put("/travelers", payload)
      : await request.post("/travelers", payload);
    if (res.code === 0) {
      showToast("保存成功");
      showPopup.value = false;
      fetchTravelers();
    } else {
      showToast(res.message || "保存失败");
    }
  } catch (error) {
    showToast(error.message || "保存失败");
  } finally {
    submitting.value = false;
  }
}

function handleDelete(item) {
  showConfirmDialog({
    title: "删除出行人",
    message: `确定要删除「${item.name}」吗？`,
  })
    .then(async () => {
      try {
        const res = await request.delete(`/travelers/${item.id}`);
        if (res.code === 0) {
          showToast("已删除");
          list.value = list.value.filter((it) => it.id !== item.id);
        } else {
          showToast(res.message || "删除失败");
        }
      } catch (error) {
        showToast(error.message || "删除失败");
      }
    })
    .catch(() => {
      // 用户取消
    });
}

onMounted(() => {
  fetchTravelers();
});
</script>

<style scoped>
.traveler-page {
  padding-bottom: 40px;
}

.traveler-card {
  margin: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.traveler-card__header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.traveler-card__name {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text);
}

.traveler-card__age {
  font-size: var(--font-size-sm);
  color: var(--color-text-light);
}

.traveler-card__row {
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--color-text);
}

.traveler-card__actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
}

.traveler-form {
  padding: var(--spacing-lg);
}

.traveler-form__title {
  margin-bottom: var(--spacing-md);
  font-size: var(--font-size-lg);
  font-weight: 600;
  text-align: center;
  color: var(--color-text);
}

.traveler-form__actions {
  margin-top: var(--spacing-xl);
}
</style>