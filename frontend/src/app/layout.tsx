import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "UGEReveVue",
  description: "A social platform for code sharing, peer reviews, and test execution.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
