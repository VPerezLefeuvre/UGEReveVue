import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "UGEReveVue",
  description: "Plateforme sociale pour développeurs : partage de code, revues de pairs et exécution de tests."
};

export default function RootLayout({
                                     children,
                                   }: Readonly<{
  children: React.ReactNode;
}>) {
  return (
      <html lang="fr">
      <body>{children}</body>
      </html>
  );
}
