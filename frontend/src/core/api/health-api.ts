import { HealthResponseSchema } from "@/core/schemas/health-schema";

export async function getBackendHealth() {
    const API_URL = process.env.BACKEND_INTERNAL_URL ?? process.env.NEXT_PUBLIC_API_URL;
    try {
        const response = await fetch(`${API_URL}/actuator/health`, { cache: 'no-store' });

        if (!response.ok) return { status: 'DOWN' };

        const rawData = await response.json();

        const result = HealthResponseSchema.safeParse(rawData);

        if (!result.success) {
            console.error("[Health Check] Schema validation failed:", result.error.message);
            return { status: 'SCHEMA_ERROR' };
        }

        return result.data;
    } catch (error) {
        console.error("Zod Validation or Fetch Error:", error);
        return { status: 'UNREACHABLE' };
    }
}
