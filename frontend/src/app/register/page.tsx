import type { Metadata } from "next";
import Link from "next/link";

import { RegisterForm } from "@/features/auth/RegisterForm";

export const metadata: Metadata = {
  title: "Register | UGEReveVue",
  description: "Create a UGEReveVue account.",
};

export default function RegisterPage() {
  return (
    <main className="min-h-screen bg-slate-50 px-4 py-10 text-slate-950 sm:px-6 lg:px-8">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-md flex-col justify-center">
        <div className="mb-8">
          <Link className="text-sm font-medium text-slate-600 hover:text-slate-950" href="/">
            UGEReveVue
          </Link>
          <h1 className="mt-6 text-3xl font-semibold tracking-normal text-slate-950">Create an account</h1>
          <p className="mt-3 text-sm leading-6 text-slate-600">
            Join the code review and sharing platform.
          </p>
        </div>

        <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <RegisterForm />
        </section>
      </div>
    </main>
  );
}
