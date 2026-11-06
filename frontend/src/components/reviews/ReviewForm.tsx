import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Controller, useForm } from "react-hook-form";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldSet,
} from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { InputGroup, InputGroupTextarea } from "@/components/ui/input-group";
import { Separator } from "@/components/ui/separator";
import { Loader, MessageSquareText } from "lucide-react";
import { Button } from "../ui/button";
import { createReviewSchema, type CreateReviewType } from "@/lib/schemas";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createReview } from "@/api/reviews";
import { toast } from "sonner";
import TooltipButton from "../common/TooltipButton";

export default function ReviewForm({ propertyId }: { propertyId: number }) {
  const queryClient = useQueryClient();

  const form = useForm<z.infer<typeof createReviewSchema>>({
    resolver: zodResolver(createReviewSchema),
    defaultValues: {
      propertyId,
      rating: 5,
      comment: "",
    },
  });

  const { mutate } = useMutation({
    mutationFn: (review: CreateReviewType) => createReview(review),
  });

  function onSubmit(data: z.infer<typeof createReviewSchema>) {
    mutate(data, {
      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: ["reviews"],
        });
        toast.success("Review was created successfully.");
      },
      onError: () => {
        toast.error("Failed to create review.");
      },
    });
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <TooltipButton tooltipText="Review" variant="outline">
          <MessageSquareText className="size-4" />
          <span className="hidden xl:flex">Review</span>
        </TooltipButton>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle className="mt-4 text-center">
            Enjoyed your stay?
          </DialogTitle>
          <Separator className="mx-auto mt-4 w-full max-w-sm" />
          <DialogDescription className="text-center leading-relaxed font-medium">
            Let others know about your experience!
          </DialogDescription>
        </DialogHeader>
        <div className="mx-auto w-full">
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <FieldGroup>
              <FieldSet>
                <FieldGroup>
                  <Controller
                    name="rating"
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field
                        orientation="vertical"
                        className="max-w-32"
                        data-invalid={fieldState.invalid}
                      >
                        <FieldLabel htmlFor="rating">Rating</FieldLabel>
                        <Select
                          name={field.name}
                          value={field.value.toString()}
                          onValueChange={field.onChange}
                        >
                          <SelectTrigger
                            id="rating"
                            aria-invalid={fieldState.invalid}
                          >
                            <SelectValue placeholder="Select" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectGroup>
                              {Array.from({ length: 5 }, (_, i) => {
                                const value = i + 1;
                                return value.toString();
                              })
                                .reverse()
                                .map((number) => (
                                  <SelectItem key={number} value={number}>
                                    {number}
                                  </SelectItem>
                                ))}
                            </SelectGroup>
                          </SelectContent>
                        </Select>
                      </Field>
                    )}
                  />

                  <Controller
                    name="comment"
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="comment">Comment</FieldLabel>
                        <InputGroup>
                          <InputGroupTextarea
                            {...field}
                            id="comment"
                            placeholder="Enter comment..."
                            rows={6}
                            className="min-h-24 resize-none"
                            aria-invalid={fieldState.invalid}
                          />
                        </InputGroup>
                        {fieldState.invalid && (
                          <FieldError errors={[fieldState.error]} />
                        )}
                      </Field>
                    )}
                  />
                </FieldGroup>
              </FieldSet>

              <Field orientation="horizontal" className="mt-4">
                <Button
                  variant="outline"
                  type="submit"
                  className="mx-auto w-full"
                  disabled={form.formState.isSubmitting}
                >
                  {form.formState.isSubmitting ? (
                    <Loader className="size-4 animate-spin" />
                  ) : (
                    "Submit"
                  )}
                </Button>
              </Field>
            </FieldGroup>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
