# CI/CD 自动化部署教程

> 本文档面向初学者，详细讲解 Mianti 项目的 GitHub Actions 自动化部署流程。

## 一、什么是 CI/CD

- **CI（Continuous Integration）持续集成**：每次提交代码后，自动运行测试，确保代码没坏
- **CD（Continuous Delivery/Deployment）持续交付/部署**：测试通过后，自动打包并部署到服务器

简单说就是：**你只需 git push，剩下的交给机器**。

## 二、整体流程

```
你在本地写代码
    ↓
git push 到 main 分支
    ↓
GitHub Actions 自动触发（约 3 分钟）
    ↓
┌─────────────────────────────────┐
│  阶段1: test（测试）              │
│  启动 MySQL + Redis 容器         │
│  运行 mvn test                   │
│  如果测试失败 → 流程终止           │
│  如果通过 → 进入阶段2              │
├─────────────────────────────────┤
│  阶段2: build（构建）             │
│  运行 mvn clean package          │
│  生成 .jar 文件                   │
│  如果失败 → 流程终止               │
│  如果通过 → 进入阶段3              │
├─────────────────────────────────┤
│  阶段3: deploy（部署）            │
│  将 .jar 传到 DigitalOcean 服务器 │
│  SSH 登录，重启 Java 服务          │
│  部署完成 ✅                      │
└─────────────────────────────────┘
```

## 三、触发条件

配置文件 `.github/workflows/ci-cd.yml` 定义了什么时候触发：

```yaml
on:
  push:
    branches: [ main, develop ]   # 推送到 main 或 develop 分支时触发全部阶段
  pull_request:
    branches: [ main ]            # PR 时只触发 test 阶段（不部署）
```

| 操作 | test | build / build-frontend | deploy |
|---|---|---|---|
| `git push` 到 `main` | ✅ | ✅ | ✅ |
| `git push` 到 `develop` | ✅ | ✅ | ✅ |
| 创建 PR 到 `main` | ✅ | ❌ | ❌ |
| `git push` 到其他分支 | ❌ | ❌ | ❌ |

## 四、四个阶段详解

### 阶段 1：test（测试）

**目的**：确保代码改动没有破坏现有功能

**执行内容**：
1. 从 GitHub 拉取最新代码（`actions/checkout`）
2. 安装 JDK 17（`actions/setup-java`）
3. 启动 MySQL 8.0 容器（密码 `test`，数据库 `mianti_test`）
4. 启动 Redis 7 容器
5. 运行 `mvn test -Dspring.profiles.active=test`
6. 上传测试覆盖率报告和测试结果作为 artifacts

**如果失败**：流程终止，不会继续构建和部署。你会收到邮件通知。

### 阶段 2：build（后端构建）

**目的**：把 Java 源码编译打包成可执行的 JAR 文件

**执行内容**：
1. 拉取代码
2. 安装 JDK 17 + 缓存 Maven 依赖
3. 运行 `mvn clean package -DskipTests`（测试已在阶段 1 跑过，这里跳过）
4. 将生成的 `.jar` 文件保存为 artifact

**关键配置**：
```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```
只在推送到 `main` 时运行。

### 阶段 3：build-frontend（前端构建）

**目的**：把前端 Vue 项目打包成静态文件

**执行内容**：
1. 拉取代码
2. 安装 Node.js 20 + 缓存 npm 依赖
3. 运行 `npm install` 安装依赖
4. 运行 `npm run build` 生成 dist 目录
5. 将 dist 目录保存为 artifact

**关键配置**：
```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```
只在推送到 `main` 时运行。

### 阶段 4：deploy（部署）

**目的**：把前后端部署到远程服务器并重启服务

**执行内容**：
1. 下载后端 JAR 文件和前端 dist 文件
2. **Tailscale 连接**：通过 Tailscale Action 加入 Tailscale 网络
3. SCP 上传后端 JAR 到 `/opt/mianti/`
4. SCP 上传前端 dist 到 `/opt/mianti/frontend/`
5. SSH 登录服务器，执行：
   - 找到正在运行的旧 Java 进程并 kill
   - 用 `nohup java -jar` 启动新的 JAR
   - 日志输出到 `/var/log/mianti/app.log`

**关键配置**：
```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```
同样只在推送到 `main` 时运行。

## 五、GitHub Secrets 配置

部署阶段需要连接服务器，敏感信息（IP、密钥）不能写在代码里，所以使用 GitHub Secrets 存储。

### 需要配置的 Secrets

| Secret 名 | 说明 | 示例值 |
|---|---|---|
| `TAILSCALE_AUTH_KEY` | Tailscale 认证密钥 | `tskey-auth-xxxxx...` |
| `DO_SSH_USER` | SSH 登录用户名 | `root` |
| `DO_SSH_KEY` | SSH 私钥内容 | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `DO_SSH_PORT` | SSH 端口（可选） | `22` |

### 如何获取 Tailscale Auth Key

1. 登录 [Tailscale 管理面板](https://login.tailscale.com/admin/settings)
2. 点击 **Settings** → **Keys**
3. 点击 **Create an ephemeral key**
4. 选择权限（建议只勾选 `Create device`）
5. 复制生成的密钥（格式：`tskey-auth-xxxxx...`）
6. 在 GitHub Secrets 中添加，名称为 `TAILSCALE_AUTH_KEY`

### 如何配置

1. 打开 GitHub 仓库页面
2. 点击 **Settings**（设置）
3. 左侧找到 **Secrets and variables** → **Actions**
4. 点击 **New repository secret**
5. 依次添加上面的 Secret

### 如何生成 SSH Key

```bash
# 本地生成 SSH 密钥（如果没有的话）
ssh-keygen -t ed25519 -C "your_email@example.com"

# 查看私钥内容（整个复制作为 DO_SSH_KEY）
cat ~/.ssh/id_ed25519

# 把公钥放到服务器上
# 公钥内容追加到服务器 ~/.ssh/authorized_keys
cat ~/.ssh/id_ed25519.pub
```

## 六、如何查看部署状态

1. 打开 GitHub 仓库页面
2. 点击顶部 **Actions** 标签
3. 点击最近的一次 workflow run
4. 点击具体的 job（test / build / deploy）查看详细日志
5. 绿色 ✅ 表示成功，红色 ❌ 表示失败

## 七、日常使用

### 正常流程

```bash
# 1. 写完代码后
git add .
git commit -m "feat: 描述你的改动"
git push origin main
```

推送完成后 GitHub 会自动触发，大约 3-5 分钟后部署完成。

### 跳过部署（只测试不部署）

如果只想跑测试但不想部署，推送到一个非 main 分支：

```bash
git push origin feature/我的功能
```

或者创建一个 PR 到 main。

### 部署失败排查

如果 deploy 阶段失败，常见原因：

| 错误 | 原因 | 解决 |
|---|---|---|
| SSH 连接失败 | Secret 配置错误或服务器 IP 变了 | 检查 Secrets 是否正确 |
| 权限不足 | SSH 用户没有 `/opt/mianti/` 目录权限 | SSH 登录服务器手动检查 |
| 端口被占用 | 旧进程没杀干净 | 在服务器上 `pgrep java` 查看 |
| Java 启动失败 | JAR 包有问题或配置错误 | 查看服务器日志 `/var/log/mianti/app.log` |

## 八、文件结构

```
.github/workflows/ci-cd.yml          # CI/CD 配置（核心文件）
pom.xml                               # Maven 构建配置（Java 版本、依赖等）
mianti-next-frontend/package.json     # 前端依赖配置
mianti-next-frontend/src/            # 前端源代码
src/main/resources/application.yml    # 通用配置
src/main/resources/application-test.yml  # 测试环境配置
src/main/resources/application-prod.yml  # 生产环境配置
```

CI/CD 中的环境变量使用：
- `test` 阶段 → 使用 `application-test.yml`
- `deploy` 阶段 → 使用 `application-prod.yml`（启动时指定 `-Dspring.profiles.active=prod`）

## 九、部署后的文件结构

服务器上 `/opt/mianti/` 目录结构：

```
/opt/mianti/
├── mianti-next-backend-1.0.0.jar    # 后端 JAR 文件
└── frontend/                         # 前端静态文件
    ├── index.html
    ├── assets/
    └── ...
```

前端通过 Nginx 或其他 Web 服务器提供静态文件服务，后端通过 Java 运行 JAR 文件提供服务。
