import type { InputHTMLAttributes } from "react";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  hasError?: boolean;
};

export function Input({ className = "", hasError = false, ...props }: InputProps) {
  return (
    <input
      className={[
        "h-11 w-full rounded-md border bg-white px-3 text-sm text-slate-950 shadow-sm transition-colors",
        "placeholder:text-slate-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-400",
        hasError ? "border-red-500 focus-visible:ring-red-300" : "border-slate-300",
        className,
      ].join(" ")}
      {...props}
    />
  );
}
