NEW_FILE_CODE
# 贡献指南 (Contributing to Mianti Next Backend)

欢迎为 **Mianti Next** 刷题平台做出贡献！为了确保项目的高效协作和代码质量，请在提交代码前仔细阅读以下规范。

## 🚀 开发工作流 (Git Flow)

我们采用标准的 Git Flow 分支管理策略：

1. **Fork & Clone**: Fork 本仓库并克隆到本地。
2. **切出分支**: 始终从 `develop` 分支切出你的功能分支。
   - 命名规范：`feature/功能名称` (如 `feature/judge-docker`) 或 `fix/问题描述`。
   - 命令：`git checkout -b feature/your-feature-name develop`
3. **开发与提交**: 
   - 遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范编写 Commit Message。
   - 示例：`feat(judge): integrate Docker for code sandbox isolation`
4. **同步代码**: 在发起 PR 前，请先同步最新的 `develop` 分支代码并解决冲突。
5. **发起 PR**: 将你的分支推送到远程，并向 `develop` 分支发起 Pull Request。

## 💻 环境与启动

### 后端 (Spring Boot)
- **要求**: Java 17+, Maven 3.6+
- **启动**: `mvn spring-boot:run`
- **测试**: `mvn test`

### 前端 (Vue 3 + Vite)
- **目录**: `mianti-next-frontend`
- **安装**: `npm install`
- **启动**: `npm run dev`

## 🛠️ 代码规范

- **Java**: 遵循阿里巴巴 Java 开发手册，使用 Lombok 简化代码。
- **TypeScript/Vue**: 遵循 ESLint + Prettier 配置，组件命名采用 PascalCase。
- **数据库**: 新增表结构请在 `sql/` 目录下同步更新 SQL 脚本。

## 📄 许可证

本项目仅供学习与交流使用。