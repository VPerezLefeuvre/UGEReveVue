import { z } from "zod";

export const RegisterSuccessSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string(),
});

export const LoginSuccessSchema = RegisterSuccessSchema.extend({
  role: z.string(),
});

export const ApiErrorSchema = z.object({
  timestamp: z.string().optional(),
  status: z.number().optional(),
  code: z.string().optional(),
  details: z.record(z.string(), z.array(z.string())).optional(),
});

export type RegisterSuccessResponse = z.infer<typeof RegisterSuccessSchema>;
export type LoginSuccessResponse = z.infer<typeof LoginSuccessSchema>;
export type ApiErrorResponse = z.infer<typeof ApiErrorSchema>;
