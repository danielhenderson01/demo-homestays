import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Button } from "../ui/button";
import type { Review } from "@/lib/types";
import { Separator } from "../ui/separator";
import { Eye } from "lucide-react";
import { Card } from "../ui/card";
import Rating from "./Rating";

export default function ReviewDetails({ review }: { review: Review }) {
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button size="icon-sm" variant="ghost">
          <Eye className="text-indigo-500" />
        </Button>
      </SheetTrigger>

      <SheetContent>
        <SheetHeader>
          <SheetTitle className="text-center">Review Details</SheetTitle>
        </SheetHeader>

        <div className="flex flex-col gap-4 px-6">
          {/* --- Image --- */}
          <Card className="mb-2 flex aspect-video overflow-hidden">
            <img
              src={review.propertyImage}
              alt="property image"
              className="w-full object-cover"
            />
          </Card>

          {/* --- Property --- */}
          <div className="flex items-center justify-between">
            <span>Property:</span>
            <div className="flex flex-col gap-1 text-right">
              <span className="truncate">{review.propertyName}</span>
            </div>
          </div>
          <Separator />

          {/* --- Guest --- */}
          <div className="flex justify-between">
            <span>Guest:</span>
            <div className="flex flex-col gap-1 text-right">
              <span className="truncate">{review.guest.name}</span>
              <span className="truncate text-blue-500">
                <a href={`mailto:${review.guest.email}`}>
                  {review.guest.email}
                </a>
              </span>
            </div>
          </div>
          <Separator />

          {/* --- Rating --- */}
          <div className="flex justify-between">
            <span>Rating:</span>
            <div className="flex flex-col gap-1 text-right">
              <Rating rating={review.rating} />
              <span className="text-xs text-muted-foreground">{`(${review.rating} stars)`}</span>
            </div>
          </div>
          <Separator />

          {/* --- Comment --- */}
          <div className="flex flex-col gap-1.5">
            <span>Comment:</span>
            <div>
              <span className="text-muted-foreground">{review.comment}</span>
            </div>
          </div>
          <Separator />
        </div>
      </SheetContent>
    </Sheet>
  );
}
