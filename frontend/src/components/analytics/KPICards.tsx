import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { formatCurrency } from "@/lib/formatters";
import { Star } from "lucide-react";
import type { BookingKPIs, ReviewKPIs } from "@/lib/types";

export default function KPICards({
  bookingKPIs,
  reviewKPIs,
}: {
  bookingKPIs: BookingKPIs;
  reviewKPIs: ReviewKPIs;
}) {
  return (
    <Card className="grid grid-cols-2 gap-4 bg-background shadow-lg @5xl/main:grid-cols-4">
      <Card className="bg-background text-center ring-0">
        <CardHeader>
          <CardDescription>Total Revenue</CardDescription>
          <CardTitle className="text-3xl font-semibold tabular-nums @[250px]/card:text-4xl">
            {formatCurrency(bookingKPIs.totalRevenue)}
          </CardTitle>
        </CardHeader>
      </Card>

      <Card className="bg-background text-center ring-0">
        <CardHeader>
          <CardDescription>Total Bookings</CardDescription>
          <CardTitle className="text-3xl font-semibold tabular-nums @[250px]/card:text-4xl">
            {bookingKPIs.totalBookings}
          </CardTitle>
        </CardHeader>
      </Card>

      <Card className="bg-background text-center ring-0">
        <CardHeader>
          <CardDescription>Average Rating</CardDescription>
          <CardTitle className="text-center text-3xl font-semibold tabular-nums @[250px]/card:text-4xl">
            <div className="flex items-center justify-center gap-2">
              {reviewKPIs.averageRating > 0 ? (
                <>
                  {reviewKPIs.averageRating}
                  <Star className="size-4.5" />
                </>
              ) : (
                <span className="text-2xl">No Ratings</span>
              )}
            </div>
          </CardTitle>
        </CardHeader>
      </Card>

      <Card className="bg-background text-center ring-0">
        <CardHeader>
          <CardDescription>Booked Nights</CardDescription>
          <CardTitle className="text-3xl font-semibold tabular-nums @[250px]/card:text-4xl">
            {bookingKPIs.bookedNights}
          </CardTitle>
        </CardHeader>
      </Card>
    </Card>
  );
}
