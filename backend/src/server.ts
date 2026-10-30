import Fastify from "fastify";
import cors from "@fastify/cors";
import helmet from "@fastify/helmet";
import { integrationRoutes } from "./routes/integrations";
import { projectRoutes } from "./routes/projects";
import { contentRoutes } from "./routes/content";
import { deploymentRoutes } from "./routes/deployments";
import { changeRequestRoutes } from "./routes/changeRequests";
import { mediaRoutes, databaseRoutes, auditRoutes } from "./routes/mediaDatabaseAudit";

const fastify = Fastify({
  logger: true
});

fastify.register(helmet);
fastify.register(cors, {
  origin: "*"
});

// Middleware: Owner Authorization Check
fastify.addHook("preHandler", async (request, reply) => {
  if (request.url.startsWith("/api/v1/health") || request.url.startsWith("/api/v1/integrations")) {
    return;
  }

  const authHeader = request.headers.authorization;
  // In production, verify JWT signed by Firebase Auth or Google Identity Services
  // For development sandbox, verify Bearer presence or authorized owner key
  if (!authHeader && process.env.NODE_ENV === "production") {
    reply.status(401).send({ error: "Unauthorized: Missing bearer authentication" });
  }
});

// 1. Health
fastify.get("/api/v1/health", async () => {
  return {
    status: "HEALTHY",
    version: "1.0.0",
    timestamp: Date.now(),
    environment: process.env.NODE_ENV || "development",
    services: {
      master_setup: "READY",
      projects_registry: "OPERATIONAL",
      content_cms: "OPERATIONAL",
      deployments_engine: "OPERATIONAL"
    }
  };
});

// 2. Register Feature Routes
fastify.register(integrationRoutes);
fastify.register(projectRoutes);
fastify.register(contentRoutes);
fastify.register(deploymentRoutes);
fastify.register(changeRequestRoutes);
fastify.register(mediaRoutes);
fastify.register(databaseRoutes);
fastify.register(auditRoutes);

const start = async () => {
  try {
    const port = Number(process.env.PORT) || 8080;
    await fastify.listen({ port, host: "0.0.0.0" });
    console.log(`Master Admin Backend API running on port ${port}`);
  } catch (err) {
    fastify.log.error(err);
    process.exit(1);
  }
};

start();
