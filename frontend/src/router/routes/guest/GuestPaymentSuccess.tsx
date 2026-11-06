import { updateBookingStatus, bookingDetailQuery } from "@/api/bookings";
import { requireRole } from "@/api/users";
import BookingInfo from "@/components/bookings/BookingInfo";
import { GUEST_PROPERTIES_PATH } from "@/lib/constants";
import { useSuspenseQuery, type QueryClient } from "@tanstack/react-query";
import { Link, useLoaderData, type LoaderFunctionArgs } from "react-router";

export const loader =
  (queryClient: QueryClient) =>
  async ({ params }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    if (!params.id) {
      throw new Response("", {
        status: 404,
        statusText: "Not Found",
      });
    }
    const bookingId = Number(params.id);
    await updateBookingStatus(bookingId, "CONFIRMED");
    await queryClient.query({
      ...bookingDetailQuery(bookingId),
      staleTime: "static",
    });
    return { bookingId };
  };

export default function GuestPaymentSuccess() {
  const { bookingId } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: booking } = useSuspenseQuery(bookingDetailQuery(bookingId));

  return (
    <div className="grid place-items-center">
      <div className="py-6 text-center">
        <p>Booking Details</p>
        <Link
          to={`${GUEST_PROPERTIES_PATH}/${booking.propertyId}`}
          className="text-3xl font-medium tracking-widest hover:text-indigo-500"
        >
          {booking.propertyName}
        </Link>
      </div>
      <div className="mx-auto w-full sm:max-w-xl">
        <BookingInfo booking={booking} />
      </div>
    </div>
  );
}
