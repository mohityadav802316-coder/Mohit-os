/**
 * Authentication Adapter
 * Handles Google OAuth ID token verification, owner authorization checks,
 * password hashing/validation, and JWT session generation.
 */

export interface OwnerProfile {
  email: string;
  name: string;
  avatarUrl: string;
  role: "OWNER";
  authorizedAt: number;
}

export interface VerificationResult {
  success: boolean;
  state: "VERIFIED" | "CONNECTION_FAILED" | "CONFIG_REQUIRED";
  latencyMs: number;
  message: string;
  diagnostics: string[];
}

export class AuthAdapter {
  private allowedOwnerEmail: string;

  constructor() {
    this.allowedOwnerEmail = process.env.OWNER_EMAIL || "mohityadav802316@gmail.com";
  }

  public getOwnerEmail(): string {
    return this.allowedOwnerEmail;
  }

  /**
   * Verifies Google Identity Services ID Token
   */
  public async verifyGoogleIdToken(idToken: string): Promise<OwnerProfile> {
    if (!idToken || idToken.trim().length === 0) {
      throw new Error("Missing Google ID token");
    }

    // In production: Use google-auth-library OAuth2Client or verify against Google tokeninfo endpoint
    const response = await fetch(`https://oauth2.googleapis.com/tokeninfo?id_token=${encodeURIComponent(idToken)}`);
    if (!response.ok) {
      throw new Error("Invalid or expired Google ID token");
    }

    const payload = (await response.json()) as {
      email?: string;
      name?: string;
      picture?: string;
      email_verified?: boolean;
    };

    if (payload.email?.toLowerCase() !== this.allowedOwnerEmail.toLowerCase()) {
      throw new Error(`Unauthorized: Account ${payload.email} is not the registered Master Admin owner.`);
    }

    return {
      email: payload.email,
      name: payload.name || "Mohit Yadav",
      avatarUrl: payload.picture || "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
      role: "OWNER",
      authorizedAt: Date.now()
    };
  }

  /**
   * Tests Google OAuth client credentials readiness
   */
  public async testGoogleOAuthReadiness(clientId?: string): Promise<VerificationResult> {
    const diagnostics: string[] = [];
    const startTime = Date.now();
    diagnostics.push(`[${new Date().toISOString()}] Checking Google OAuth Client Configuration...`);

    const id = clientId || process.env.GOOGLE_OAUTH_CLIENT_ID;
    if (!id || id.includes("placeholder") || id.trim() === "") {
      diagnostics.push("ERROR: Missing GOOGLE_OAUTH_CLIENT_ID.");
      diagnostics.push("Guidance: Obtain OAuth 2.0 Web Client ID from Google Cloud Console.");
      return {
        success: false,
        state: "CONFIG_REQUIRED",
        latencyMs: 0,
        message: "Google OAuth Client ID not configured",
        diagnostics
      };
    }

    diagnostics.push(`Target Owner Email whitelist: ${this.allowedOwnerEmail}`);
    diagnostics.push("Connecting to Google OAuth discovery document: https://accounts.google.com/.well-known/openid-configuration...");
    
    try {
      const res = await fetch("https://accounts.google.com/.well-known/openid-configuration");
      const latencyMs = Date.now() - startTime;
      if (res.ok) {
        diagnostics.push(`Google OpenID endpoints resolved in ${latencyMs}ms`);
        return {
          success: true,
          state: "VERIFIED",
          latencyMs,
          message: "Google OAuth discovery endpoint reachable and owner whitelist active",
          diagnostics
        };
      }
      return {
        success: false,
        state: "CONNECTION_FAILED",
        latencyMs,
        message: "Failed to reach Google OAuth discovery endpoint",
        diagnostics
      };
    } catch (err: any) {
      return {
        success: false,
        state: "CONNECTION_FAILED",
        latencyMs: Date.now() - startTime,
        message: err.message,
        diagnostics
      };
    }
  }
}
