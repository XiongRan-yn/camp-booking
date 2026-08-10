import { createApp } from "vue";
import { createPinia } from "pinia";
import router from "./router";
import App from "./App.vue";

// 全局样式
import "./styles/reset.css";
import "./styles/variables.css";
import "./styles/common.css";

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount("#app");
