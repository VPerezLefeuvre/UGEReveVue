import { en, translateErrorSlug } from "@/core/i18n/en";
import { ApiErrorSchema, RegisterSuccessSchema, type ApiErrorResponse } from "@/core/schemas/auth-schema";

export type RegisterPayload = {
  username: string;
  email: string;
  password: string;
};

export type RegisterFieldErrors = Partial<Record<keyof RegisterPayload, string[]>>;

type RegisterSuccess = {
  ok: true;
  message: string;
};

type RegisterFailure = {
  ok: false;
  fieldErrors?: RegisterFieldErrors;
  message: string;
};

export type RegisterResult = RegisterSuccess | RegisterFailure;

const FIELD_NAMES = ["username", "email", "password"] as const;

export async function registerUser(payload: RegisterPayload): Promise<RegisterResult> {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL;

  if (!apiUrl) {
    return {
      ok: false,
      message: en.auth.register.missingApiUrl,
    };
  }

  try {
    const response = await fetch(`${apiUrl}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      await readRegisterSuccess(response);

      return {
        ok: true,
        message: en.auth.register.success,
      };
    }

    const apiError = await readApiError(response);
    const fieldErrors = mapFieldErrors(apiError);

    return {
      ok: false,
      fieldErrors,
      message: response.status >= 500 ? en.auth.register.unavailable : resolveGlobalMessage(apiError, fieldErrors),
    };
  } catch {
    return {
      ok: false,
      message: en.auth.register.unreachable,
    };
  }
}

async function readRegisterSuccess(response: Response): Promise<void> {
  const contentType = response.headers.get("content-type");

  if (!contentType?.includes("application/json")) {
    return;
  }

  const body = (await response.json()) as unknown;
  RegisterSuccessSchema.safeParse(body);
}

async function readApiError(response: Response): Promise<ApiErrorResponse | undefined> {
  const contentType = response.headers.get("content-type");

  if (!contentType?.includes("application/json")) {
    return undefined;
  }

  const body = (await response.json()) as unknown;
  const result = ApiErrorSchema.safeParse(body);

  return result.success ? result.data : undefined;
}

function mapFieldErrors(apiError: ApiErrorResponse | undefined): RegisterFieldErrors | undefined {
  if (!apiError?.details) {
    return undefined;
  }

  const errors: RegisterFieldErrors = {};

  for (const field of FIELD_NAMES) {
    const slugs = apiError.details[field];

    if (slugs?.length) {
      errors[field] = slugs.map(translateErrorSlug);
    }
  }

  return Object.keys(errors).length > 0 ? errors : undefined;
}

function resolveGlobalMessage(
  apiError: ApiErrorResponse | undefined,
  fieldErrors: RegisterFieldErrors | undefined,
): string {
  if (fieldErrors) {
    return en.auth.register.validationFailed;
  }

  const firstDetailSlug = apiError?.details ? Object.values(apiError.details).flat()[0] : undefined;

  return translateErrorSlug(firstDetailSlug ?? apiError?.code);
}
