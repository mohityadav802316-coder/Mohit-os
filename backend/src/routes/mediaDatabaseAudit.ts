import { FastifyInstance } from "fastify";
import { StorageAdapter } from "../adapters/storageAdapter";
import { PostgresAdapter } from "../adapters/postgresAdapter";

export async function mediaRoutes(fastify: FastifyInstance) {
  const storageAdapter = new StorageAdapter();

  const mediaRegistry = [
    {
      id: "med_avatar_01",
      filename: "mohit_profile_headshot.jpg",
      mimeType: "image/jpeg",
      url: "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
      sizeBytes: 420800,
      uploadedAt: Date.now() - 604800000,
      linkedProjects: ["proj_mohitport"]
    },
    {
      id: "med_banner_01",
      filename: "cybermohit_matrix_banner.png",
      mimeType: "image/png",
      url: "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5",
      sizeBytes: 1240000,
      uploadedAt: Date.now() - 259200000,
      linkedProjects: ["proj_mohitport", "proj_docintel"]
    }
  ];

  fastify.get("/api/v1/media", async () => {
    return mediaRegistry;
  });

  fastify.post("/api/v1/media/upload-url", async (request) => {
    const { filename, mimeType } = (request.body || {}) as { filename: string; mimeType: string };
    return storageAdapter.createPresignedUpload(filename || "upload.bin", mimeType || "application/octet-stream");
  });

  fastify.post("/api/v1/media/register", async (request, reply) => {
    const body = request.body as any;
    if (!body || !body.url) {
      return reply.status(400).send({ error: "Media URL is required" });
    }

    const newAsset = {
      id: `med_${Date.now().toString(36)}`,
      filename: body.filename || "asset.jpg",
      mimeType: body.mimeType || "image/jpeg",
      url: body.url,
      sizeBytes: body.sizeBytes || 102400,
      uploadedAt: Date.now(),
      linkedProjects: body.linkedProjects || ["proj_mohitport"]
    };

    mediaRegistry.unshift(newAsset);
    return reply.status(201).send(newAsset);
  });
}

export async function databaseRoutes(fastify: FastifyInstance) {
  const postgresAdapter = new PostgresAdapter();

  fastify.get("/api/v1/database/tables", async () => {
    return postgresAdapter.listTables();
  });
}

export async function auditRoutes(fastify: FastifyInstance) {
  const auditLogs = [
    {
      id: "aud_01",
      actor: "Mohit Yadav",
      action: "SETUP_WIZARD_ACCESSED",
      target: "Master Setup Center",
      timestamp: Date.now() - 300000,
      correlationId: "corr_setup_init_01",
      status: "SUCCESS"
    },
    {
      id: "aud_02",
      actor: "Mohit Yadav",
      action: "INTEGRATION_TEST_EXECUTED",
      target: "Vercel Hosting & Deployments",
      timestamp: Date.now() - 180000,
      correlationId: "corr_test_ver_88",
      status: "SUCCESS"
    },
    {
      id: "aud_03",
      actor: "System Scheduler",
      action: "HEALTH_CHECK_COMPLETED",
      target: "https://mohitport.vercel.app",
      timestamp: Date.now() - 60000,
      correlationId: "corr_ping_839",
      status: "SUCCESS"
    }
  ];

  fastify.get("/api/v1/audit-logs", async () => {
    return auditLogs;
  });

  fastify.post("/api/v1/audit-logs", async (request) => {
    const body = request.body as any;
    const entry = {
      id: `aud_${Date.now().toString(36)}`,
      actor: body.actor || "Mohit Yadav",
      action: body.action || "USER_ACTION",
      target: body.target || "Master Admin",
      timestamp: Date.now(),
      correlationId: body.correlationId || `corr_${Date.now()}`,
      status: body.status || "SUCCESS"
    };
    auditLogs.unshift(entry);
    return entry;
  });
}
