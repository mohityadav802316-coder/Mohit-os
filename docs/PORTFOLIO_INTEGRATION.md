# CYBERMOHIT AI PORTFOLIO — INTEGRATION GUIDE

## 1. Context & Overview
- **Project Name**: CyberMohit AI Portfolio (MohitPort)
- **Primary Owner**: Mohit Yadav (`mohityadav802316@gmail.com`)
- **Deployment Platform**: Vercel
- **Production URL**: `https://mohitport.vercel.app`
- **Development / Preview**: `https://ais-dev-bzaqgkqspnan7kpwg5zyto-924569394319.asia-southeast1.run.app`

---

## 2. End-to-End Content Synchronization Flow
Saving content in the Master Admin Android application communicates directly with the configured backend API, which commits changes to the database and instructs Vercel to invalidate edge caches or trigger an ISR (Incremental Static Regeneration) rebuild.

```
Master Admin (Android Client)
         │
         │ PUT /api/v1/projects/proj_mohitport/content/hero
         ▼
Master Admin Backend API (Fastify / Cloud Run)
         │
         ├─► Commits JSON payload to PostgreSQL (`content_sections`)
         ├─► Emits append-only Audit Log (`UPDATE_WEBSITE_HERO`)
         └─► Invalidation Webhook to Vercel Deploy Hook
                  │
                  ▼
         Vercel Edge Network
         Updates `https://mohitport.vercel.app`
```

---

## 3. Integration Checklist & Step-by-Step Setup

### Step 1: Connect Vercel Hosting
1. Generate a Vercel Personal Access Token from your Vercel Account Settings -> Tokens.
2. In the AI Studio Secrets panel or backend environment configuration, set:
   ```
   VERCEL_AUTH_TOKEN=your_token_here
   VERCEL_PROJECT_ID=prj_cybermohit
   VERCEL_TEAM_ID=team_mohit (optional)
   ```
3. Test integration via Master Admin app: Navigate to `Integrations` -> Click `Ping Test` next to Vercel.

### Step 2: Connect GitHub Repository
1. In repository settings on GitHub, add the Master Admin webhook URL:
   `https://<master-admin-api>/api/v1/webhooks/github`
2. Configure secret for webhook signature verification.
3. Master Admin will now automatically index branches, commit SHAs, and preview deployments.

### Step 3: Website Headless CMS Hook
Ensure `mohitport.vercel.app` fetches its content dynamically or via Next.js ISR:
```typescript
// in src/app/page.tsx or api/content.ts
export async function getPortfolioData() {
  const res = await fetch("https://<master-admin-api>/api/v1/projects/proj_mohitport/content", {
    next: { revalidate: 60 } // revalidates every 60 seconds or on-demand
  });
  return res.json();
}
```
When changes are published in the Android Content Manager, the website fetches the updated Hero headline, bio, skills, and project cards instantly!
