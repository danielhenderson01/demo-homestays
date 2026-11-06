import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { formatCurrency } from "@/lib/formatters";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import PageHeader from "@/components/common/PageHeader";
import BookingDetailsSheet from "@/components/bookings/BookingDetailsSheet";
import { useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { hostBookingListQuery } from "@/api/bookings";
import { requireRole } from "@/api/users";
import EmptyResource from "@/components/common/EmptyResource";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["HOST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    await queryClient.query({
      ...hostBookingListQuery(searchParams.toString()),
      staleTime: "static",
    });
    return { searchParams };
  };

export default function HostBookings() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    hostBookingListQuery(searchParams.toString())
  );

  const { data: bookings, meta } = result;

  return (
    <>
      <PageHeader
        title="Bookings"
        subtitle="Manage bookings for your properties"
      />

      <div className="flex flex-col rounded-lg border p-4">
        {bookings.length < 1 ? (
          <EmptyResource kind="booking" />
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Property</TableHead>
                <TableHead>Guest</TableHead>
                <TableHead>Total</TableHead>
                <TableHead className="text-center">Details</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {bookings.map((booking) => (
                <TableRow key={`${booking.id}-${booking.propertyId}`}>
                  <TableCell className="truncate">
                    {booking.propertyName}
                  </TableCell>
                  <TableCell className="truncate">
                    {booking.guest.name}
                  </TableCell>
                  <TableCell>{formatCurrency(booking.orderTotal)}</TableCell>
                  <TableCell className="text-center">
                    <BookingDetailsSheet booking={booking} />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
