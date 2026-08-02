"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle, CheckCircle2, Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { Alert } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { Input } from "@/components/ui/input";
import { loginUser } from "@/core/api/auth-api";
import { loginSchema, type LoginFormValues } from "@/features/auth/login-schema";

type Feedback =
  | {
      tone: "success" | "error";
      message: string;
    }
  | undefined;

export function LoginForm() {
  const router = useRouter();
  const [feedback, setFeedback] = useState<Feedback>();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    mode: "onTouched",
    defaultValues: {
      email: "",
      password: "",
    },
  });

  async function onSubmit(values: LoginFormValues) {
    setFeedback(undefined);

    const result = await loginUser(values);

    if (result.ok) {
      setFeedback({
        tone: "success",
        message: result.message,
      });
      router.push("/");
      return;
    }

    if (result.fieldErrors) {
      for (const [field, messages] of Object.entries(result.fieldErrors)) {
        setError(field as keyof LoginFormValues, {
          type: "server",
          message: messages?.join(" "),
        });
      }
    }

    setFeedback({
      tone: "error",
      message: result.message,
    });
  }

  return (
    <form className="grid gap-5" noValidate onSubmit={handleSubmit(onSubmit)}>
      {feedback ? (
        <Alert tone={feedback.tone}>
          <div className="flex items-start gap-2">
            {feedback.tone === "success" ? (
              <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0" />
            ) : (
              <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
            )}
            <span>{feedback.message}</span>
          </div>
        </Alert>
      ) : null}

      <FormField error={errors.email?.message} htmlFor="email" label="Email">
        <Input
          autoComplete="email"
          hasError={Boolean(errors.email)}
          id="email"
          type="email"
          {...register("email")}
        />
      </FormField>

      <FormField error={errors.password?.message} htmlFor="password" label="Password">
        <Input
          autoComplete="current-password"
          hasError={Boolean(errors.password)}
          id="password"
          type="password"
          {...register("password")}
        />
      </FormField>

      <Button className="mt-1 w-full" disabled={isSubmitting} type="submit">
        {isSubmitting ? (
          <>
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Signing in
          </>
        ) : (
          "Sign in"
        )}
      </Button>
    </form>
  );
}
