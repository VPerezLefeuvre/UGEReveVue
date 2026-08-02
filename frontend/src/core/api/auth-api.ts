import { en, translateErrorSlug } from "@/core/i18n/en";
import {
  ApiErrorSchema,
  LoginSuccessSchema,
  RegisterSuccessSchema,
  type ApiErrorResponse,
  type LoginSuccessResponse,
} from "@/core/schemas/auth-schema";

export type RegisterPayload = {
  username: string;
  email: string;
  password: string;
};

export type LoginPayload = {
  email: string;
  password: string;
};

export type RegisterFieldErrors = Partial<Record<keyof RegisterPayload, string[]>>;
export type LoginFieldErrors = Partial<Record<keyof LoginPayload, string[]>>;

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

type LoginSuccess = {
  ok: true;
  message: string;
  user: LoginSuccessResponse;
};

type LoginFailure = {
  ok: false;
  fieldErrors?: LoginFieldErrors;
  message: string;
};

export type LoginResult = LoginSuccess | LoginFailure;

const REGISTER_FIELD_NAMES = ["username", "email", "password"] as const;
const LOGIN_FIELD_NAMES = ["email", "password"] as const;
const AUTH_STORAGE_KEY = "ugerevevue.authUser";

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
    const fieldErrors = mapFieldErrors(apiError, REGISTER_FIELD_NAMES);

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

export async function loginUser(payload: LoginPayload): Promise<LoginResult> {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL;

  if (!apiUrl) {
    return {
      ok: false,
      message: en.auth.login.missingApiUrl,
    };
  }

  try {
    const response = await fetch(`${apiUrl}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      const user = await readLoginSuccess(response);
      storeAuthenticatedUser(user);

      return {
        ok: true,
        user,
        message: en.auth.login.success,
      };
    }

    const apiError = await readApiError(response);
    const fieldErrors = mapFieldErrors(apiError, LOGIN_FIELD_NAMES);

    return {
      ok: false,
      fieldErrors,
      message: response.status >= 500 ? en.auth.login.unavailable : resolveLoginGlobalMessage(apiError, fieldErrors),
    };
  } catch {
    return {
      ok: false,
      message: en.auth.login.unreachable,
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

async function readLoginSuccess(response: Response): Promise<LoginSuccessResponse> {
  const body = (await response.json()) as unknown;
  const result = LoginSuccessSchema.safeParse(body);

  if (!result.success) {
    throw new Error("Invalid login response");
  }

  return result.data;
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

function mapFieldErrors<TField extends string>(
  apiError: ApiErrorResponse | undefined,
  fieldNames: readonly TField[],
): Partial<Record<TField, string[]>> | undefined {
  if (!apiError?.details) {
    return undefined;
  }

  const errors: Partial<Record<TField, string[]>> = {};

  for (const field of fieldNames) {
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

function resolveLoginGlobalMessage(
  apiError: ApiErrorResponse | undefined,
  fieldErrors: LoginFieldErrors | undefined,
): string {
  if (fieldErrors) {
    return en.auth.login.validationFailed;
  }

  const firstDetailSlug = apiError?.details ? Object.values(apiError.details).flat()[0] : undefined;

  return translateErrorSlug(firstDetailSlug ?? apiError?.code);
}

function storeAuthenticatedUser(user: LoginSuccessResponse): void {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
}
