import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useTransition } from "react";
import { GUEST_PROPERTIES_PATH } from "@/lib/constants";
import { formatCurrency } from "@/lib/formatters";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import DeleteDialog from "@/components/common/DeleteDialog";
import PageHeader from "@/components/common/PageHeader";
import { Link, useLoaderData, type LoaderFunctionArgs } from "react-router";
import {
  QueryClient,
  useMutation,
  useQueryClient,
  useSuspenseQuery,
} from "@tanstack/react-query";
import { deleteBooking, guestBookingListQuery } from "@/api/bookings";
import { toast } from "sonner";
import { Separator } from "@/components/ui/separator";
import { requireRole } from "@/api/users";
import BookingInfo from "@/components/bookings/BookingInfo";
import Modal from "@/components/common/Modal";
import { Button } from "@/components/ui/button";
import { Info } from "lucide-react";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    await queryClient.query({
      ...guestBookingListQuery(searchParams.toString()),
      staleTime: "static",
    });
    return { searchParams };
  };

export default function GuestBookings() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    guestBookingListQuery(searchParams.toString())
  );

  const queryClient = useQueryClient();

  const { mutate } = useMutation({
    mutationFn: (id: number) => deleteBooking(id),
  });

  const [isPending, startTransition] = useTransition();

  const handleDelete = (id: number) => {
    startTransition(async () => {
      mutate(id, {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: ["bookings"],
          });
          toast.success("Booking was deleted.");
        },
        onError: () => {
          toast.error("Failed to delete booking.");
        },
      });
    });
  };

  const { data: bookings, meta } = result;

  return (
    <>
      <PageHeader title="Bookings" subtitle="Manage your bookings" />
      <div className="flex flex-col rounded-lg border p-4">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Property</TableHead>
              <TableHead>Total</TableHead>
              <TableHead>Check In</TableHead>
              <TableHead>Check Out</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {bookings.map((booking) => (
              <TableRow key={`${booking.id}-${booking.propertyId}`}>
                <TableCell>
                  <Link
                    to={`${GUEST_PROPERTIES_PATH}/${booking.propertyId}`}
                    className="truncate hover:text-indigo-500"
                  >
                    {booking.propertyName}
                  </Link>
                </TableCell>
                <TableCell>{formatCurrency(booking.orderTotal)}</TableCell>
                <TableCell>{booking.checkIn}</TableCell>
                <TableCell>{booking.checkOut}</TableCell>
                <TableCell className="flex items-center gap-3">
                  <Modal
                    dialogTitle="Booking Details"
                    dialogDescription={booking.propertyName}
                    dialogTrigger={
                      <Button variant="ghost" size="icon-sm">
                        <Info className="size-4 text-indigo-500" />
                      </Button>
                    }
                  >
                    <BookingInfo booking={booking} />
                  </Modal>
                  <Separator orientation="vertical" />
                  <DeleteDialog
                    id={booking.id}
                    deleteAction={handleDelete}
                    isPending={isPending}
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
