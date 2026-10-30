import { FastifyInstance } from "fastify";

export async function changeRequestRoutes(fastify: FastifyInstance) {
  const requests = [
    {
      id: "cr_8831a",
      projectId: "proj_mohitport",
      title: "Update Vercel Edge Headers",
      reason: "Optimize CDN caching and add Strict-Transport-Security header for cybermohit portfolio",
      requestType: "CONFIG_UPDATE",
      status: "PENDING_APPROVAL",
      riskLevel: "MEDIUM",
      targetEnvironment: "PRODUCTION",
      diffSummary: "vercel.json max-age update and security headers",
      beforeValue: '{\n  "headers": [\n    { "source": "/(.*)", "headers": [{ "key": "Cache-Control", "value": "max-age=0" }] }\n  ]\n}',
      afterValue: '{\n  "headers": [\n    { "source": "/(.*)", "headers": [\n      { "key": "Cache-Control", "value": "public, max-age=86400, stale-while-revalidate=600" },\n      { "key": "Strict-Transport-Security", "value": "max-age=63072000; includeSubDomains; preload" }\n    ] }\n  ]\n}',
      proposedAt: Date.now() - 3600000,
      approvedAt: null,
      approvedBy: null
    },
    {
      id: "cr_7729b",
      projectId: "proj_mohitport",
      title: "Introduce Jetpack Compose Hero Showcase",
      reason: "Showcase native Android architectural expertise directly on the web portfolio hero banner",
      requestType: "CONTENT_CHANGE",
      status: "PENDING_APPROVAL",
      riskLevel: "LOW",
      targetEnvironment: "PRODUCTION",
      diffSummary: "Hero badge and subtitle update",
      beforeValue: '"subtitle": "Principal AI Architect"',
      afterValue: '"subtitle": "Principal AI Architect & Senior Android Engineer"',
      proposedAt: Date.now() - 7200000,
      approvedAt: null,
      approvedBy: null
    }
  ];

  // GET /api/v1/change-requests
  fastify.get("/api/v1/change-requests", async () => {
    return requests;
  });

  // POST /api/v1/change-requests
  fastify.post("/api/v1/change-requests", async (request, reply) => {
    const body = request.body as any;
    const newRequest = {
      id: `cr_${Date.now().toString(36)}`,
      projectId: body.projectId || "proj_mohitport",
      title: body.title || "Proposed Change",
      reason: body.reason || "Operational enhancement",
      requestType: body.requestType || "CONFIG_UPDATE",
      status: "PENDING_APPROVAL",
      riskLevel: body.riskLevel || "LOW",
      targetEnvironment: body.targetEnvironment || "PRODUCTION",
      diffSummary: body.diffSummary || "Configuration delta",
      beforeValue: body.beforeValue || "",
      afterValue: body.afterValue || "",
      proposedAt: Date.now(),
      approvedAt: null,
      approvedBy: null
    };

    requests.unshift(newRequest);
    return reply.status(201).send(newRequest);
  });

  // POST /api/v1/change-requests/:id/approve
  fastify.post("/api/v1/change-requests/:id/approve", async (request, reply) => {
    const { id } = request.params as { id: string };
    const cr = requests.find(r => r.id === id);
    if (!cr) {
      return reply.status(404).send({ error: `Change request '${id}' not found` });
    }

    cr.status = "APPROVED";
    cr.approvedAt = Date.now() as any;
    cr.approvedBy = "Mohit Yadav" as any;

    return {
      success: true,
      requestId: cr.id,
      status: cr.status,
      approvedBy: cr.approvedBy,
      approvedAt: cr.approvedAt
    };
  });

  // POST /api/v1/change-requests/:id/reject
  fastify.post("/api/v1/change-requests/:id/reject", async (request, reply) => {
    const { id } = request.params as { id: string };
    const cr = requests.find(r => r.id === id);
    if (!cr) {
      return reply.status(404).send({ error: `Change request '${id}' not found` });
    }

    cr.status = "REJECTED";
    return {
      success: true,
      requestId: cr.id,
      status: cr.status
    };
  });
}
