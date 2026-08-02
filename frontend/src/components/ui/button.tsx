import type { ButtonHTMLAttributes } from "react";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement>;

export function Button({ className = "", type = "button", ...props }: ButtonProps) {
  return (
    <button
      type={type}
      className={[
        "inline-flex h-11 items-center justify-center rounded-md bg-slate-950 px-4 text-sm font-medium text-white",
        "transition-colors hover:bg-slate-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-400",
        "disabled:pointer-events-none disabled:opacity-60",
        className,
      ].join(" ")}
      {...props}
    />
  );
}
