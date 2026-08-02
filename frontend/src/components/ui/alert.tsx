import type { ReactNode } from "react";

type AlertProps = {
  tone: "success" | "error";
  children: ReactNode;
};

export function Alert({ tone, children }: AlertProps) {
  const className =
    tone === "success"
      ? "border-emerald-200 bg-emerald-50 text-emerald-800"
      : "border-red-200 bg-red-50 text-red-800";

  return <div className={`rounded-md border px-4 py-3 text-sm ${className}`}>{children}</div>;
}
