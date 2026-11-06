import type { RatingResponse } from "@/lib/types";
import Rating from "../reviews/Rating";
import { Star } from "lucide-react";

export default function PropertyRating({
  rating,
  size,
}: {
  rating: RatingResponse;
  size: "sm" | "lg";
}) {
  if (rating.averageRating === 0 || rating.numRatings === 0) return null;

  return (
    <span className="flex items-center gap-2 text-sm font-medium">
      {size === "sm" ? (
        <Star fill="var(--foreground)" className="size-4" />
      ) : (
        <Rating rating={rating.averageRating} />
      )}
      {rating.averageRating.toFixed(1)}{" "}
    </span>
  );
}
