import type { Review } from "@/lib/types";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import Rating from "./Rating";
import { Separator } from "../ui/separator";
import Comment from "./Comment";
import { GUEST_PROPERTIES_PATH } from "@/lib/constants";
import { useTransition } from "react";
import DeleteDialog from "../common/DeleteDialog";
import { Link } from "react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { deleteReview } from "@/api/reviews";

export default function GuestReviewCard({ review }: { review: Review }) {
  const [isPending, startTransition] = useTransition();

  const queryClient = useQueryClient();

  const { mutate } = useMutation({
    mutationFn: (id: number) => deleteReview(id),
  });

  const handleDelete = (id: number) => {
    startTransition(async () => {
      mutate(id, {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: ["reviews"],
          });
          toast.success("Review was deleted.");
        },
        onError: () => {
          toast.error("Failed to delete review.");
        },
      });
    });
  };

  return (
    <Card className="flex flex-col">
      <Link to={`${GUEST_PROPERTIES_PATH}/${review.propertyId}`}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex flex-col gap-2">
              <p className="text-sm tracking-wide text-muted-foreground">
                {review.propertyName}
              </p>
              <Rating rating={review.rating} />
            </div>
            <div className="mr-1">
              <img
                src={review.propertyImage}
                alt="property"
                className="aspect-square size-16 rounded object-cover"
              />
            </div>
          </div>
          <Separator />
        </CardHeader>
      </Link>
      <CardContent className="flex flex-col">
        <Comment comment={review.comment} />
        <div className="ml-auto inline-flex">
          <DeleteDialog
            id={review.id}
            deleteAction={handleDelete}
            isPending={isPending}
          />
        </div>
      </CardContent>
    </Card>
  );
}
