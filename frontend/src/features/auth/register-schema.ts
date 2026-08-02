import { z } from "zod";

import { en } from "@/core/i18n/en";

const PASSWORD_SPECIAL_CHARACTER = /[@#$%^&+=!]/;
const messages = en.validation;

export const registerSchema = z.object({
  username: z
    .string()
    .min(1, messages.USERNAME_REQUIRED)
    .min(3, messages.USERNAME_SIZE_INVALID)
    .max(20, messages.USERNAME_SIZE_INVALID)
    .regex(/^[a-zA-Z0-9_]*$/, messages.USERNAME_PATTERN_INVALID),
  email: z
    .string()
    .min(1, messages.EMAIL_REQUIRED)
    .max(100, messages.EMAIL_SIZE_INVALID)
    .email(messages.EMAIL_FORMAT_INVALID),
  password: z
    .string()
    .min(1, messages.PASSWORD_REQUIRED)
    .min(8, messages.PASSWORD_SIZE_INVALID)
    .max(128, messages.PASSWORD_SIZE_INVALID)
    .regex(/[A-Z]/, messages.PASSWORD_MISSING_UPPERCASE)
    .regex(/[0-9]/, messages.PASSWORD_MISSING_DIGIT)
    .regex(PASSWORD_SPECIAL_CHARACTER, messages.PASSWORD_MISSING_SPECIAL),
});

export type RegisterFormValues = z.infer<typeof registerSchema>;
