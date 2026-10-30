import { FastifyInstance } from "fastify";

export interface ProjectEntity {
  id: string;
  name: string;
  description: string;
  url: string;
  icon: string;
  project_type: string;
  environment: string;
  health_status: string;
  repository_url: string;
  hosting_provider: string;
  database_provider: string;
  last_sync_timestamp: number;
}

export async function projectRoutes(fastify: FastifyInstance) {
  // In-memory persistent registry for local execution before cloud database activation
  const registry: ProjectEntity[] = [
    {
      id: "proj_mohitport",
      name: "CyberMohit AI Portfolio",
      description: "Primary personal portfolio website built on AI Studio and deployed on Vercel",
      url: "https://mohitport.vercel.app",
      icon: "terminal",
      project_type: "PORTFOLIO",
      environment: "PRODUCTION",
      health_status: "HEALTHY",
      repository_url: "github.com/mohityadav/cybermohit-portfolio",
      hosting_provider: "VERCEL",
      database_provider: "POSTGRESQL",
      last_sync_timestamp: Date.now()
    },
    {
      id: "proj_docintel",
      name: "DocIntel AI Gateway",
      description: "Autonomous multimodal legal document intelligence pipeline",
      url: "https://docintel.cybermohit.io",
      icon: "file_text",
      project_type: "WEB_APP",
      environment: "PRODUCTION",
      health_status: "HEALTHY",
      repository_url: "github.com/mohityadav/docintel-pipeline",
      hosting_provider: "CLOUD_RUN",
      database_provider: "POSTGRESQL",
      last_sync_timestamp: Date.now() - 3600000
    },
    {
      id: "proj_edgeai",
      name: "EdgeAI Android Hub",
      description: "On-device neural inference and sensor pipeline engine for Android",
      url: "https://edgeai.cybermohit.io",
      icon: "cpu",
      project_type: "MOBILE_APP",
      environment: "STAGING",
      health_status: "HEALTHY",
      repository_url: "github.com/mohityadav/edgeai-android",
      hosting_provider: "PLAY_STORE",
      database_provider: "ROOM_SQLITE",
      last_sync_timestamp: Date.now() - 7200000
    }
  ];

  // GET /api/v1/projects
  fastify.get("/api/v1/projects", async () => {
    return registry;
  });

  // GET /api/v1/projects/:id
  fastify.get("/api/v1/projects/:id", async (request, reply) => {
    const { id } = request.params as { id: string };
    const project = registry.find(p => p.id === id);
    if (!project) {
      return reply.status(404).send({ error: `Project '${id}' not found` });
    }
    return project;
  });

  // POST /api/v1/projects
  fastify.post("/api/v1/projects", async (request, reply) => {
    const body = request.body as Partial<ProjectEntity>;
    if (!body.name || !body.url) {
      return reply.status(400).send({ error: "Project name and URL are required" });
    }

    const newProject: ProjectEntity = {
      id: `proj_${Date.now().toString(36)}`,
      name: body.name,
      description: body.description || "Connected project service",
      url: body.url,
      icon: body.icon || "web",
      project_type: body.project_type || "WEB_APP",
      environment: body.environment || "PRODUCTION",
      health_status: "HEALTHY",
      repository_url: body.repository_url || "",
      hosting_provider: body.hosting_provider || "VERCEL",
      database_provider: body.database_provider || "POSTGRESQL",
      last_sync_timestamp: Date.now()
    };

    registry.push(newProject);
    return reply.status(201).send(newProject);
  });

  // DELETE /api/v1/projects/:id
  fastify.delete("/api/v1/projects/:id", async (request, reply) => {
    const { id } = request.params as { id: string };
    const index = registry.findIndex(p => p.id === id);
    if (index === -1) {
      return reply.status(404).send({ error: `Project '${id}' not found` });
    }
    const removed = registry.splice(index, 1)[0];
    return { success: true, removedProjectId: removed.id };
  });
}
