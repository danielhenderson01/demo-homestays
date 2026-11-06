import { ax } from "./axiosInstance";
import { queryOptions } from "@tanstack/react-query";
import type { CreateReviewType } from "../lib/schemas";
import type { Review, APISuccessResponse } from "../lib/types";

export async function createReview(review: CreateReviewType): Promise<Review> {
  const response = await ax.post<Review>("/reviews", review);
  return { ...response.data };
}

export async function deleteReview(id: number): Promise<void> {
  await ax.delete(`/reviews/${id}`);
}

export function propertyReviewListQuery(
  propertyId: number,
  params: string = ""
) {
  return queryOptions({
    queryKey: ["reviews", "list", propertyId, params],
    queryFn: async (): Promise<APISuccessResponse<Review[]>> => {
      const response = await ax.get<APISuccessResponse<Review[]>>(
        `/reviews/property/${propertyId}?${params}`
      );
      return { ...response.data };
    },
  });
}

export function hostReviewListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["reviews", "list", params],
    queryFn: async (): Promise<APISuccessResponse<Review[]>> => {
      const response = await ax.get<APISuccessResponse<Review[]>>(
        `/reviews?${params}`
      );
      return { ...response.data };
    },
  });
}

export function guestReviewListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["reviews", "list", params],
    queryFn: async (): Promise<APISuccessResponse<Review[]>> => {
      const response = await ax.get<APISuccessResponse<Review[]>>(
        `/reviews/guest?${params}`
      );
      return { ...response.data };
    },
  });
}

export function existsGuestReviewQuery(propertyId: number) {
  return queryOptions({
    queryKey: ["reviews", propertyId],
    queryFn: async (): Promise<Review | null> => {
      const response = await ax.get<Review | null>(
        `/reviews/property/${propertyId}/guest`
      );
      return response.data;
    },
  });
}
