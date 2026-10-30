# MASTER ADMIN — SECURITY THREAT CHECKLIST & POLICIES

## 1. Zero-Trust Access & Identity Control
- [x] **Owner Authentication**: Full access restricted to Mohit Yadav (`mohityadav802316@gmail.com`).
- [x] **Server-Side Authorization**: Every state-changing API request is validated on the backend. Client role tampering is rejected with HTTP 403.
- [x] **No Plaintext Secrets in Client**: The Android APK does NOT package or expose Vercel tokens, GitHub PATs, database credentials, or secret keys.

## 2. Infrastructure & API Security
- [x] **HTTPS / TLS Enforced**: All communication with the Master Admin backend and external services is encrypted in transit.
- [x] **SSRF Protection**: Remote media URLs and webhook triggers are validated against strict whitelist schemas (`https://` only, localhost and private RFC 1918 IPs blocked).
- [x] **SQL Injection Defense**: All database operations use parameterized queries or type-safe ORM abstractions.
- [x] **Idempotency Safeguards**: Sensitive operations (e.g. deployments, batch content updates) require unique idempotency keys to prevent duplicate actions.

## 3. Governance & Change Control
- [x] **Approval Gating**: Critical changes (code patches, database schema changes, production deployments) must be submitted as Change Requests and explicitly approved by the Owner.
- [x] **Append-Only Audit Trail**: Every sensitive operation records an immutable audit log with actor, action, timestamp, and correlation ID.
- [x] **Destructive Action Confirmation**: Deleting a project registration from Master Admin strictly preserves remote repositories, databases, and Vercel hosting.
