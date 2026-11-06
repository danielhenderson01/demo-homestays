import { Star } from "lucide-react";

export default function Rating({ rating }: { rating: number }) {
  const stars = Array.from({ length: 5 }, (_, i) => i + 1 <= rating);

  return (
    <div className="flex items-center gap-1">
      {stars.map((isFilled, i) =>
        isFilled ? (
          <Star
            fill="var(--foreground)"
            strokeWidth={0}
            className="size-3.5"
            key={i}
          />
        ) : (
          <Star
            fill="var(--muted)"
            className="size-3.5"
            strokeWidth={0}
            key={i}
          />
        )
      )}
    </div>
  );
}
