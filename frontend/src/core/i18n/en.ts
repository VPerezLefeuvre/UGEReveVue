export const en = {
  auth: {
    register: {
      success: "Your account has been created.",
      validationFailed: "Please fix the highlighted fields and try again.",
      unavailable: "The server is unavailable. Please try again later.",
      unreachable: "Unable to contact the server.",
      missingApiUrl: "Missing API configuration: NEXT_PUBLIC_API_URL.",
      unknownError: "Something went wrong. Please try again.",
    },
  },
  validation: {
    USERNAME_REQUIRED: "Username is required.",
    USERNAME_SIZE_INVALID: "Username must be between 3 and 20 characters.",
    USERNAME_PATTERN_INVALID: "Username can only contain letters, numbers, and underscores.",
    USERNAME_ALREADY_EXISTS: "This username is already taken.",
    EMAIL_REQUIRED: "Email is required.",
    EMAIL_SIZE_INVALID: "Email must be 100 characters or fewer.",
    EMAIL_FORMAT_INVALID: "Enter a valid email address.",
    EMAIL_ALREADY_EXISTS: "This email is already registered.",
    PASSWORD_REQUIRED: "Password is required.",
    PASSWORD_SIZE_INVALID: "Password must be between 8 and 128 characters.",
    PASSWORD_MISSING_UPPERCASE: "Password must include at least one uppercase letter.",
    PASSWORD_MISSING_DIGIT: "Password must include at least one number.",
    PASSWORD_MISSING_SPECIAL: "Password must include at least one special character (@#$%^&+=!).",
    DATABASE_CONFLICT: "This account cannot be created with the submitted information.",
    DATA_INTEGRITY_ERROR: "This account cannot be created with the submitted information.",
    USER_ALREADY_EXISTS: "An account with these details already exists.",
    VALIDATION_FAILED: "Please fix the highlighted fields and try again.",
  },
} as const;

export type MessageSlug = keyof typeof en.validation;

export function translateErrorSlug(slug: string | undefined): string {
  if (!slug) {
    return en.auth.register.unknownError;
  }

  return en.validation[slug as MessageSlug] ?? en.auth.register.unknownError;
}
