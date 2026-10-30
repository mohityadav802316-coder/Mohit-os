# MASTER ADMIN — API CONTRACT & SPECIFICATION

**Base Path**: `/api/v1`  
**Authentication**: `Authorization: Bearer <OWNER_JWT_TOKEN>`  
**Correlation Header**: `X-Correlation-ID: corr_<uuid>`  
**Idempotency Header**: `Idempotency-Key: idemp_<uuid>` (Required for POST/PUT)

---

## 1. System Health
### `GET /api/v1/health`
Checks backend API, database connectivity, and external provider reachability.

**Response 200 OK**:
```json
{
  "status": "HEALTHY",
  "version": "1.0.0",
  "timestamp": 1793280000000,
  "environment": "production",
  "services": {
    "database": "CONNECTED",
    "vercel_adapter": "HEALTHY",
    "github_adapter": "HEALTHY"
  }
}
```

---

## 2. Projects Registry
### `GET /api/v1/projects`
Lists all non-archived projects.

**Response 200 OK**:
```json
[
  {
    "id": "proj_mohitport",
    "name": "CyberMohit AI Portfolio",
    "description": "Primary personal portfolio website built on AI Studio and deployed on Vercel",
    "url": "https://mohitport.vercel.app",
    "icon": "terminal",
    "project_type": "PORTFOLIO",
    "environment": "PRODUCTION",
    "health_status": "HEALTHY",
    "repository_url": "github.com/mohityadav/cybermohit-portfolio",
    "hosting_provider": "VERCEL",
    "database_provider": "POSTGRESQL",
    "last_sync_timestamp": 1793280000000
  }
]
```

### `POST /api/v1/projects`
Registers a new project.

**Request Body**:
```json
{
  "name": "Core Gateway API",
  "description": "Central Fastify API microservice",
  "url": "https://api.internal.com",
  "project_type": "BACKEND_API",
  "environment": "STAGING",
  "repository_url": "github.com/mohityadav/api-gateway",
  "hosting_provider": "CLOUD_RUN",
  "database_provider": "POSTGRESQL"
}
```

---

## 3. Website Content Management (CMS)
### `GET /api/v1/projects/:id/content`
Fetches structured CMS content for the website.

**Response 200 OK**:
```json
{
  "project_id": "proj_mohitport",
  "hero": {
    "title": "Mohit Yadav",
    "subtitle": "Principal AI Architect & Senior Android Engineer",
    "badge": "Available for High-Impact Projects",
    "bio": "Crafting production-grade multi-project cloud systems, native Android applications, and autonomous AI architectures.",
    "avatar_url": "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
    "cta_text": "Explore Systems & Deployments",
    "cta_url": "#projects"
  },
  "skills": [
    { "id": "sk_1", "name": "Kotlin & Jetpack Compose", "category": "Mobile", "proficiency": 98 }
  ],
  "projects": [
    {
      "id": "p_1",
      "title": "Master Admin Control Center",
      "description": "Private multi-website DevOps command center.",
      "tags": ["Kotlin", "Compose", "Vercel", "PostgreSQL"],
      "live_url": "https://mohitport.vercel.app",
      "github_url": "https://github.com/mohityadav/master-admin"
    }
  ],
  "sync_status": "LIVE_ON_WEBSITE",
  "last_sync_time": 1793280000000
}
```

### `PUT /api/v1/projects/:id/content/hero`
Updates the hero content section.

**Request Body**:
```json
{
  "title": "Mohit Yadav",
  "subtitle": "Principal AI Architect & Senior Android Engineer",
  "badge": "Available for High-Impact Projects",
  "bio": "Crafting production-grade systems with unmatched precision.",
  "avatar_url": "https://images.unsplash.com/sample.jpg",
  "cta_text": "Explore Systems",
  "cta_url": "#projects"
}
```

**Response 200 OK**:
```json
{
  "success": true,
  "message": "Hero content updated and committed to database",
  "timestamp": 1793280000000,
  "sync_status": "SAVED_TO_BACKEND"
}
```

---

## 4. Change Requests & Approvals
### `POST /api/v1/change-requests`
Submits a proposed change for owner review.

**Request Body**:
```json
{
  "project_id": "proj_mohitport",
  "title": "Update Vercel Edge Headers",
  "reason": "Optimize CDN caching performance",
  "request_type": "CONFIG_UPDATE",
  "risk_level": "MEDIUM",
  "target_environment": "PREVIEW",
  "diff_summary": "vercel.json max-age update",
  "before_value": "max-age=0",
  "after_value": "max-age=86400"
}
```

### `POST /api/v1/change-requests/:id/approve`
Approves and triggers execution. Only executable by authenticated Owner.

**Response 200 OK**:
```json
{
  "success": true,
  "request_id": "cr_8831a",
  "status": "EXECUTED",
  "executed_at": 1793280000000,
  "executed_by": "Mohit Yadav"
}
```

---

## 5. Deployments
### `POST /api/v1/deployments/trigger`
Triggers a deployment on Vercel.

**Request Body**:
```json
{
  "project_id": "proj_mohitport",
  "target_environment": "PRODUCTION",
  "commit_sha": "a4f91bc",
  "branch": "main",
  "approved_by": "Mohit Yadav"
}
```

**Response 202 Accepted**:
```json
{
  "deployment_id": "dpl_ver_01",
  "status": "BUILDING",
  "url": "https://mohitport.vercel.app",
  "timestamp": 1793280000000
}
```
