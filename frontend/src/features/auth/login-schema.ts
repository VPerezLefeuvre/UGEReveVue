import { z } from "zod";

import { en } from "@/core/i18n/en";

const messages = en.validation;

export const loginSchema = z.object({
  email: z
    .string()
    .min(1, messages.EMAIL_REQUIRED)
    .email(messages.EMAIL_FORMAT_INVALID),
  password: z.string().min(1, messages.PASSWORD_REQUIRED),
});

export type LoginFormValues = z.infer<typeof loginSchema>;
