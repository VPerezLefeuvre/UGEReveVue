import Link from "next/link";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-slate-50 px-6 py-16 text-slate-950">
      <div className="w-full max-w-2xl">
        <h1 className="text-4xl font-semibold tracking-normal">UGEReveVue</h1>
        <p className="mt-4 max-w-xl text-base leading-7 text-slate-600">
          A social platform for developers to share code and review work together.
        </p>
        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            className="inline-flex h-11 items-center justify-center rounded-md bg-slate-950 px-4 text-sm font-medium text-white transition-colors hover:bg-slate-800"
            href="/register"
          >
            Create account
          </Link>
          <Link
            className="inline-flex h-11 items-center justify-center rounded-md border border-slate-300 bg-white px-4 text-sm font-medium text-slate-900 transition-colors hover:bg-slate-100"
            href="/login"
          >
            Sign in
          </Link>
          <Link
            className="inline-flex h-11 items-center justify-center rounded-md border border-slate-300 bg-white px-4 text-sm font-medium text-slate-900 transition-colors hover:bg-slate-100"
            href="/status"
          >
            View status
          </Link>
        </div>
      </div>
    </main>
  );
}
