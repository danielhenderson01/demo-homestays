import { Navigation2 } from "lucide-react";

export default function Footer() {
  return (
    <footer className="border-t bg-neutral-100/90 py-6 dark:bg-background/90">
      <div className="container mx-auto flex w-full max-w-7xl flex-col items-center justify-between px-8 sm:flex-row">
        <div>
          <Navigation2 className="size-6 text-foreground hover:cursor-pointer hover:text-foreground/90" />
        </div>
        <div>
          <p className="mt-3 text-xs font-medium md:mt-0 dark:text-muted-foreground">
            &copy; 2026 Demo Homestays
          </p>
        </div>
      </div>
    </footer>
  );
}
