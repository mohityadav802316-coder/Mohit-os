# MASTER ADMIN — ARCHITECTURE SPECIFICATION

## 1. System Mission
"Master Admin" is a private, production-grade native Android command center engineered for **Mohit Yadav** (`mohityadav802316@gmail.com`). It serves as the authoritative interface for managing existing and future websites, web applications, backend services, databases, repositories, hosting platforms, and deployments.

The initial managed project is the personal portfolio website:
- **Project**: CyberMohit AI Portfolio (MohitPort)
- **Deployment Platform**: Vercel Edge Network
- **Production URL**: `https://mohitport.vercel.app`
- **Source Repository**: `github.com/mohityadav/cybermohit-portfolio`

---

## 2. Architectural Layers

```
┌─────────────────────────────────────────────────────────────┐
│                 NATIVE ANDROID CLIENT (KOTLIN)              │
│  - Jetpack Compose M3 UI (Obsidian Cyber Theme)             │
│  - Navigation Compose & ViewModel Flow Architecture         │
│  - Room Database (Local Caching & Offline Sync)             │
│  - Retrofit HTTP Client & Moshi Serialization               │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / TLS (Bearer Auth)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│             MASTER ADMIN BACKEND API (FASTIFY / TS)         │
│  - Server-Side Role Authorization & Owner Verification       │
│  - Idempotency Keys & Correlation Tracking (UUIDv4)          │
│  - Change Request Approval Engine                           │
│  - Audit Logging Pipeline (Append-Only)                     │
└──────┬───────────────────────┬───────────────────────┬──────┘
       │                       │                       │
       ▼                       ▼                       ▼
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ VERCEL API   │       │ GITHUB API   │       │ POSTGRESQL   │
│ - Deployments│       │ - Branches   │       │ - Relational │
│ - Edge Logs  │       │ - Commits    │       │   Data Store │
│ - Previews   │       │ - Diffs      │       │ - CMS Tables │
└──────────────┘       └──────────────┘       └──────────────┘
```

### A. Presentation Layer (Jetpack Compose & Material 3)
- **Design Language**: Dark-first Cyber Obsidian theme (`#090D16`), Electric Cyan (`#00F0FF`), Deep Navy (`#0F172A`), Status Emerald (`#10B981`), Warning Amber (`#F59E0B`), and Rose Red (`#EF4444`).
- **Navigation Structure**:
  - `Dashboard`: Aggregated system metrics, project health, pending approvals preview, live hosting status.
  - `Projects`: Multi-project registry with filtering by type (`PORTFOLIO`, `WEB_APP`, `BACKEND_API`, `MOBILE_APP`), search, registration modal, and destructive protection.
  - `Project Control Hub`: Deep multi-tab workspace for a project (Overview, CMS Editor, Git/Code Viewer, Deployments, Database Explorer, Media Assets, Integrations).
  - `Change Requests & Approvals`: Gated change management center preventing unreviewed production mutations.
  - `Deployments`: Vercel release management, live build logs, preview-to-production promotion.
  - `Settings`: Owner identity verification, backend API routing, system snapshot export.

### B. Domain Layer
- Type-safe domain models decoupled from persistence models.
- Business rules enforcing least-privilege operations and mandatory change request reviews.

### C. Data & Persistence Layer
- **Room Database (`MasterAdminDatabase`)**: Local SQLite persistence for offline capability and instantaneous UI rendering.
- **Repository Pattern (`MasterAdminRepository`)**: Single source of truth coordinating local database queries and cloud API synchronization.
- **Network API (`MasterAdminApi`)**: REST API client using Retrofit 2 and OkHttp with logging interceptors.

---

## 3. Security Boundaries & Zero-Trust Policies
1. **No Client-Side Secrets**: Vercel API tokens, GitHub personal access tokens, database passwords, and private certificates are strictly isolated on the server side or in Google Cloud Secrets / AI Studio Secrets panel.
2. **Owner Role Verification**: All sensitive operations (publishing content, approving code patches, triggering production builds) require explicit Owner role confirmation (`mohityadav802316@gmail.com`).
3. **SSRF Prevention**: All external URLs and webhook destinations are validated against allowed protocols (`https://`) and sanitized.
4. **Append-Only Audit Trail**: Every administrative action (project creation, content edit, approval, deployment) records an immutable audit log with a unique correlation ID.
