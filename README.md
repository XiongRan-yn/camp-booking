# 研学营/民宿预订系统

> 大一 Web 开发课题 | 7人小组 | 2026年8月

## 技术栈

| 层 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Vant 4 + Vue Router + Pinia + Axios |
| 后端 | Spring Boot 3.x + MyBatis-Plus + Spring Security + JWT |
| 数据库 | H2（内嵌，零配置） |
| 构建 | Maven（后端）+ npm（前端） |

## 快速启动

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`，H2 数据库自动初始化。

H2 控制台：`http://localhost:8080/h2-console`

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`

### 3. 线上演示（答辩用）

```bash
ngrok http 5173
```

将生成的公网网址发给老师即可。

## 项目结构

```
camp-booking/
├── frontend/          # Vue 3 前端
│   └── src/
│       ├── api/       # 接口请求
│       ├── components/# 通用组件
│       ├── router/    # 路由配置
│       ├── stores/    # Pinia 状态
│       ├── styles/    # 全局样式
│       └── views/     # 页面
├── backend/           # Spring Boot 后端
│   └── src/main/java/com/example/campbooking/
│       ├── config/    # 配置类
│       ├── controller/# 控制器
│       ├── service/   # 业务逻辑
│       ├── mapper/    # MyBatis 映射
│       ├── entity/    # 实体类
│       ├── dto/       # 请求对象
│       ├── vo/        # 返回对象
│       ├── common/    # 公共类
│       └── security/  # 安全/JWT
└── docs/              # 项目文档
```

## 开发规范

详见 `docs/DEVELOPMENT_STANDARDS.docx`

- Git 分支：`main` ← `develop` ← `feature/xxx`
- Commit：`feat:` / `fix:` / `style:` / `docs:`
- 前端 ESLint + Prettier，后端阿里巴巴规范
- 接口统一返回 `{ code, message, data }`

## 组员分工

详见 `docs/TASK_ASSIGNMENT.docx`
