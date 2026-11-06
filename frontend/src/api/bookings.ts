import { queryOptions } from "@tanstack/react-query";
import { ax } from "./axiosInstance";
import { createBookingSchema } from "../lib/schemas";
import type {
  APISuccessResponse,
  Booking,
  BookingInfo,
  StripeResponse,
} from "../lib/types";
import { toast } from "sonner";

export async function createBooking(formData: FormData) {
  try {
    const booking = createBookingSchema.parse({
      propertyId: Number(formData.get("propertyId")),
      checkIn: String(formData.get("checkIn")),
      checkOut: String(formData.get("checkOut")),
    });
    const response = await ax.post<StripeResponse>("/bookings", {
      propertyId: booking.propertyId,
      checkIn: booking.checkIn.split("T")[0],
      checkOut: booking.checkOut.split("T")[0],
    });
    window.location.href = response.data.paymentUrl;
  } catch (error) {
    toast.error("Failed to create booking");
  }
}

export async function updateBookingStatus(
  id: number,
  bookingStatus: "CONFIRMED" | "CANCELLED"
) {
  const response = await ax.put<Booking>(`/bookings/${id}`, {
    id,
    bookingStatus,
  });
  return { ...response.data };
}

export async function deleteBooking(id: number): Promise<void> {
  await ax.delete(`/bookings/${id}`);
}

export function bookingDetailQuery(id: number) {
  return queryOptions({
    queryKey: ["bookings", "detail", id],
    queryFn: async (): Promise<Booking> => {
      try {
        const response = await ax.get<Booking>(`/bookings/${id}`);
        return { ...response.data };
      } catch (error) {
        throw new Response("", {
          status: 404,
          statusText: "Not Found",
        });
      }
    },
  });
}

export function hostBookingListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["bookings", "list", params],
    queryFn: async (): Promise<APISuccessResponse<Booking[]>> => {
      const response = await ax.get<APISuccessResponse<Booking[]>>(
        `/bookings/host?${params}`
      );
      return { ...response.data };
    },
  });
}

export function guestBookingListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["bookings", "list", params],
    queryFn: async () => {
      const response = await ax.get<APISuccessResponse<Booking[]>>(
        `/bookings/guest?${params}`
      );
      return { ...response.data };
    },
  });
}

export function bookingInfoDetailQuery(propertyId: number) {
  return queryOptions({
    queryKey: ["bookingInfo", propertyId],
    queryFn: async (): Promise<BookingInfo> => {
      try {
        const response = await ax.get<BookingInfo>(
          `/bookings/property/${propertyId}`
        );
        return { ...response.data };
      } catch (error) {
        throw new Response("", {
          status: 404,
          statusText: "Not Found",
        });
      }
    },
  });
}
