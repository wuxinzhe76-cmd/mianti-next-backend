# 部署与配置指南

## 🔧 环境变量配置

本项目使用环境变量管理敏感配置，**请勿将真实配置提交到代码库**。

### 1. 创建 `.env` 文件

```bash
cp .env.example .env
```

### 2. 填写真实配置

编辑 `.env` 文件，替换所有 `your_*` 占位符为真实值。

---

## 📦 必需配置项

| 配置项 | 获取方式 | 说明 |
|--------|---------|------|
| `SENTRY_DSN` | [sentry.io](https://sentry.io) → 创建项目 → Settings → Client Keys | 错误监控必需 |
| `DASHSCOPE_API_KEY` | 阿里云 DashScope 控制台 | AI 功能必需 |
| `DB_*` | DigitalOcean 数据库控制台 | 生产数据库 |
| `REDIS_*` | DigitalOcean Redis 控制台 | 缓存服务 |
| `DO_DROPLET_IP` | DigitalOcean Droplet IP | 部署服务器 |
| `DO_SSH_KEY` | 本地 `~/.ssh/id_rsa` 内容 | SSH 私钥（用于部署） |

### 可选配置项

| 配置项 | 说明 |
|--------|------|
| `NACOS_*` | 如果使用配置中心则填写 |
| `ELASTICSEARCH_URI` | 如果使用 Elasticsearch 则填写 |
| `WX_APP_*` | 如果使用微信登录则填写 |

---

## 🚀 GitHub Actions 配置

在 GitHub 仓库的 **Settings → Secrets and variables → Actions** 中添加：

| Secret | 值 |
|--------|---|
| `DO_DROPLET_IP` | Droplet IP 地址 |
| `DO_SSH_USER` | SSH 用户名（通常 root） |
| `DO_SSH_KEY` | SSH 私钥内容 |
| `DO_SSH_PORT` | SSH 端口（默认 22） |

配置后，每次 push 到 main 分支会自动：
1. ✅ 运行测试
2. ✅ 构建 JAR
3. ✅ 部署到 DigitalOcean

---

## 🧪 测试运行

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 生成覆盖率报告
mvn test
# 报告位置：target/site/jacoco/index.html
```

---

## 🗄️ 数据库迁移（Flyway）

默认关闭，如需启用：

```yaml
spring:
  flyway:
    enabled: true
```

迁移脚本位置：`src/main/resources/db/migration/`

命名规则：`V{版本号}__{描述}.sql`

示例：
```
V1__init.sql
V2__add_user_table.sql
V3__add_index.sql
```

---

## 🔐 安全提醒

- ✅ 永远不要提交 `.env` 文件
- ✅ 数据库密码使用环境变量
- ✅ API Key 使用环境变量
- ✅ SSH Key 存储在 GitHub Secrets
- ✅ 生产环境开启 HTTPS（cookie secure 已配置）
