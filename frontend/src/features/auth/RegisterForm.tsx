"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle, CheckCircle2, Loader2 } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { Alert } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { Input } from "@/components/ui/input";
import { registerUser } from "@/core/api/auth-api";
import { registerSchema, type RegisterFormValues } from "@/features/auth/register-schema";

type Feedback =
  | {
      tone: "success" | "error";
      message: string;
    }
  | undefined;

export function RegisterForm() {
  const [feedback, setFeedback] = useState<Feedback>();
  const {
    register,
    handleSubmit,
    setError,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    mode: "onTouched",
    defaultValues: {
      username: "",
      email: "",
      password: "",
    },
  });

  async function onSubmit(values: RegisterFormValues) {
    setFeedback(undefined);

    const result = await registerUser(values);

    if (result.ok) {
      reset();
      setFeedback({
        tone: "success",
        message: result.message,
      });
      return;
    }

    if (result.fieldErrors) {
      for (const [field, messages] of Object.entries(result.fieldErrors)) {
        setError(field as keyof RegisterFormValues, {
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

      <FormField error={errors.username?.message} htmlFor="username" label="Username">
        <Input
          autoComplete="username"
          hasError={Boolean(errors.username)}
          id="username"
          {...register("username")}
        />
      </FormField>

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
          autoComplete="new-password"
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
            Creating account
          </>
        ) : (
          "Create account"
        )}
      </Button>
    </form>
  );
}
