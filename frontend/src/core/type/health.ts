import { z } from 'zod';

export const HealthResponseSchema = z.object({
    status: z.string(),
    components: z.object({
        db: z.object({
            status: z.string(),
            details: z.object({
                database: z.string(),
                validationQuery: z.string().optional(),
            }).optional(),
        }).optional(),
    }).optional(),
});

export type HealthResponse = z.infer<typeof HealthResponseSchema>;
