import { FastifyInstance } from "fastify";

export async function contentRoutes(fastify: FastifyInstance) {
  // Headless CMS content state for CyberMohit AI Portfolio
  let cmsState = {
    projectId: "proj_mohitport",
    hero: {
      title: "Mohit Yadav",
      subtitle: "Principal AI Architect & Senior Android Engineer",
      badge: "Available for High-Impact Projects",
      bio: "Crafting production-grade multi-project cloud systems, native Android applications, and autonomous AI architectures with unmatched precision.",
      avatarUrl: "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
      ctaText: "Explore Systems & Deployments",
      ctaUrl: "#projects"
    },
    skills: [
      { id: "sk_1", name: "Kotlin & Jetpack Compose", category: "Mobile", proficiency: 98 },
      { id: "sk_2", name: "Autonomous AI Agents", category: "AI Systems", proficiency: 95 },
      { id: "sk_3", name: "TypeScript & Fastify", category: "Backend", proficiency: 94 },
      { id: "sk_4", name: "PostgreSQL & Neon Cloud", category: "Databases", proficiency: 92 },
      { id: "sk_5", name: "Vercel Edge & Cloudflare R2", category: "DevOps", proficiency: 90 }
    ],
    projects: [
      {
        id: "p_1",
        title: "Master Admin Control Center",
        description: "Private multi-website DevOps command center with real-time CMS, GitHub diffs, and Vercel edge deployment triggers.",
        tags: ["Kotlin", "Jetpack Compose", "Vercel API", "PostgreSQL", "Room"],
        liveUrl: "https://mohitport.vercel.app",
        githubUrl: "https://github.com/mohityadav/master-admin",
        featured: true
      },
      {
        id: "p_2",
        title: "DocIntel Autonomous Pipeline",
        description: "Multimodal document extraction engine powering instant legal summary contracts with 99.8% precision.",
        tags: ["Python", "FastAPI", "Gemini 1.5 Pro", "Vector DB"],
        liveUrl: "https://docintel.cybermohit.io",
        githubUrl: "https://github.com/mohityadav/docintel-pipeline",
        featured: true
      }
    ],
    syncStatus: "LIVE_ON_WEBSITE",
    lastSyncTime: Date.now()
  };

  // GET /api/v1/projects/:id/content
  fastify.get("/api/v1/projects/:id/content", async (request) => {
    return cmsState;
  });

  // PUT /api/v1/projects/:id/content/hero
  fastify.put("/api/v1/projects/:id/content/hero", async (request, reply) => {
    const body = request.body as any;
    if (!body || !body.title) {
      return reply.status(400).send({ error: "Title is required" });
    }

    cmsState.hero = {
      ...cmsState.hero,
      ...body
    };
    cmsState.lastSyncTime = Date.now();
    cmsState.syncStatus = "PENDING_DEPLOYMENT";

    return {
      success: true,
      message: "Hero section updated in CMS state",
      hero: cmsState.hero,
      syncStatus: cmsState.syncStatus,
      timestamp: Date.now()
    };
  });

  // PUT /api/v1/projects/:id/content/skills
  fastify.put("/api/v1/projects/:id/content/skills", async (request) => {
    const { skills } = request.body as { skills: any[] };
    if (Array.isArray(skills)) {
      cmsState.skills = skills;
      cmsState.lastSyncTime = Date.now();
      cmsState.syncStatus = "PENDING_DEPLOYMENT";
    }
    return {
      success: true,
      skills: cmsState.skills,
      syncStatus: cmsState.syncStatus
    };
  });

  // PUT /api/v1/projects/:id/content/projects
  fastify.put("/api/v1/projects/:id/content/projects", async (request) => {
    const { projects } = request.body as { projects: any[] };
    if (Array.isArray(projects)) {
      cmsState.projects = projects;
      cmsState.lastSyncTime = Date.now();
      cmsState.syncStatus = "PENDING_DEPLOYMENT";
    }
    return {
      success: true,
      projects: cmsState.projects,
      syncStatus: cmsState.syncStatus
    };
  });
}
