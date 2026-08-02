import type { Metadata } from "next";
import Link from "next/link";

import { LoginForm } from "@/features/auth/LoginForm";

export const metadata: Metadata = {
  title: "Login | UGEReveVue",
  description: "Sign in to your UGEReveVue account.",
};

export default function LoginPage() {
  return (
    <main className="min-h-screen bg-slate-50 px-4 py-10 text-slate-950 sm:px-6 lg:px-8">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-md flex-col justify-center">
        <div className="mb-8">
          <Link className="text-sm font-medium text-slate-600 hover:text-slate-950" href="/">
            UGEReveVue
          </Link>
          <h1 className="mt-6 text-3xl font-semibold tracking-normal text-slate-950">Sign in</h1>
          <p className="mt-3 text-sm leading-6 text-slate-600">
            Access your account to continue reviewing and sharing code.
          </p>
        </div>

        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <LoginForm />
        </section>

        <p className="mt-6 text-center text-sm text-slate-600">
          No account yet?{" "}
          <Link className="font-medium text-slate-950 hover:underline" href="/register">
            Create one
          </Link>
        </p>
      </div>
    </main>
  );
}
