# 比价宝项目

这是一个基于Vue 3 + Vite的比价宝前端项目。

## 问题解决

### 500错误问题
项目之前出现500错误是因为：
1. Vue文件格式问题（已修复）
2. 缺少后端API服务器（已添加模拟服务器）

## 安装依赖

```bash
npm install
```

## 运行项目

### 方式1：只运行前端（推荐用于开发）
```bash
npm run dev
```

### 方式2：同时运行前端和后端
```bash
npm run dev:full
```

### 方式3：分别运行
```bash
# 终端1：运行后端服务器
npm run server

# 终端2：运行前端开发服务器
npm run dev
```

## 测试账号

后端服务器已配置测试账号：
- 用户名：admin
- 密码：123456

## 项目结构

```
src/
├── components/          # 公共组件
├── pages/              # 页面组件
│   ├── home/           # 首页相关
│   ├── user/           # 用户相关（登录、注册等）
│   ├── admin/          # 管理后台
│   └── post/           # 动态相关
├── router/             # 路由配置
└── main.js             # 应用入口
```

## 技术栈

- Vue 3
- Vue Router 4
- Vite
- Element Plus
- Axios
- Chart.js
- ECharts

## API接口

项目配置了以下API接口：
- POST /api/login - 用户登录
- POST /api/register - 用户注册
- GET /api/health - 健康检查

所有API请求会通过Vite代理转发到后端服务器（localhost:3001）。
