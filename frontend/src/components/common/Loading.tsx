import { Loader2 } from "lucide-react";

export default function Loading() {
  return (
    <div className="fixed inset-0 flex items-center justify-center gap-2 bg-background/50">
      <Loader2 className="size-12 animate-spin text-primary/60" />
      <span className="font-medium text-primary/60">Loading...</span>
    </div>
  );
}
