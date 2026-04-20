# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Mianti** is a Question Bank Management System with AI-powered interview capabilities. It consists of two main components:

- **Backend**: Spring Boot 3.2.4 + Java 17 + MyBatis-Plus (`mianti-next-backend/`)
- **Frontend**: Vue 3 + TypeScript + Vite + Element Plus (`mianti-next-frontend/`)

## Key Commands

### Backend (mianti-next-backend/)
```bash
# Build
mvn clean package

# Run (dev profile)
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run with test profile
mvn test -Dspring.profiles.active=test

# Generate JaCoCo coverage report
mvn test
# Report: target/site/jacoco/index.html
```

### Frontend (mianti-next-frontend/)
```bash
# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Type check + build
npm run build  # runs vue-tsc -b && vite build
```

## Architecture

### Backend Package Structure (`com.charles.mianti`)

| Package | Purpose |
|---------|---------|
| `controller/` | REST API endpoints (7 controllers: User, QuestionBank, Question, QuestionBankQuestion, Judge, TestCase, Interview) |
| `service/` (+ `impl/`) | Business logic layer |
| `mapper/` | MyBatis-Plus data access layer |
| `model/` | `entity/` (DB entities), `dto/` (request DTOs), `vo/` (response VOs), `enums/` |
| `aop/` | Auth interceptor (`AuthInterceptor`), logging (`LogInterceptor`) |
| `satoken/` | Sa-Token authentication integration |
| `sentinel/` | Rate limiting via Alibaba Sentinel |
| `blackfilter/` | IP blacklist filtering |
| `config/` | Spring configuration classes |
| `esdao/` | Elasticsearch data access |
| `common/` | `BaseResponse`, `ErrorCode`, `ResultUtils` |
| `exception/` | `BusinessException`, `ThrowUtils` |
| `judge/` | Code judging logic |
| `wxmp/` | WeChat Open Platform integration |

### Core Entities

- **User** - User accounts with role-based access (admin/user)
- **QuestionBank** - Collections of questions
- **Question** - Individual questions with title, content, tags, answer, difficulty, type
- **QuestionBankQuestion** - Many-to-many relationship between banks and questions
- **TestCase** - Test cases for code judging
- **Submission** - Code submission records
- **JudgeResult** - Code evaluation results
- **ProgrammingLanguage** - Supported programming languages
- **InterviewSession** / **InterviewRecord** - AI interview sessions

### Key Integrations

- **Auth**: Sa-Token for session/role management
- **DB**: MySQL + MyBatis-Plus + Druid connection pool + Flyway migrations
- **Cache**: Redis + Redisson distributed lock
- **Search**: Elasticsearch (for question search)
- **Hot Key**: JD HotKey client with etcd + Caffeine local cache
- **Rate Limiting**: Alibaba Sentinel
- **Config Center**: Nacos
- **AI**: Spring AI Alibaba (DashScope/Qwen model)
- **Error Monitoring**: Sentry
- **API Docs**: Knife4j (Swagger) at `/api/doc.html`
- **WeChat Login**: WxJava MP SDK

### Frontend Structure

- `/src/views/` - Page components (auth, dashboard, questionBank, question, questionBankQuestion)
- `/src/stores/` - Pinia state management
- `/src/services/` - API service layer (Axios)
- `/src/router/` - Vue Router with auth guards
- `/src/components/` - Reusable UI components
- `/src/types/` - TypeScript type definitions
- API base URL: `/api` (proxied via Vite)

## Configuration

- Environment variables: see `.env.example` for all required keys
- Backend config: `src/main/resources/application.yml` (common), `application-dev.yml`, `application-test.yml`, `application-prod.yml`
- Server runs on port **8101** with context path `/api`
- Flyway migrations: `src/main/resources/db/migration/` (disabled by default, set `FLYWAY_ENABLED=true`)

## CI/CD

GitHub Actions workflow (`.github/workflows/ci-cd.yml`):
1. **test** - Runs `mvn test` with MySQL + Redis services (triggered on push/PR)
2. **build** - Builds JAR on push to `main` (after tests pass)
3. **deploy** - Deploys JAR to DigitalOcean Droplet via SSH on push to `main`

## Development Notes

- All API responses use `BaseResponse<T>` with `code` (0 = success), `message`, `data`
- Logical deletion uses `isDelete` field (1 = deleted, 0 = active)
- Admin-only endpoints check `StpUtil.hasRole("admin")`
- The `hotkey-client` JAR is a local dependency in `lib/` (uses `system` scope)
