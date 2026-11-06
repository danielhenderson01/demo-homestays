import { hostDashboardQuery } from "@/api/analytics";
import KPICards from "@/components/analytics/KPICards";
import ChartLine from "@/components/analytics/ChartLine";
import { formatMonth } from "@/lib/formatters";
import { useSuspenseQuery, type QueryClient } from "@tanstack/react-query";
import { ChartBar } from "@/components/analytics/ChartBar";
import BookingStatusChart from "@/components/analytics/BookingStatusChart";
import { requireRole } from "@/api/users";

export const loader = (queryClient: QueryClient) => async () => {
  await requireRole(queryClient, ["HOST"]);
  await queryClient.query({ ...hostDashboardQuery(), staleTime: "static" });
};

export default function HostDashboard() {
  const { data: result } = useSuspenseQuery(hostDashboardQuery());

  const {
    bookingKPIs,
    reviewKPIs,
    revenueByMonth,
    bookingsByMonth,
    ratingDistribution,
    bookingStatusDistribution,
  } = result;

  return (
    <div className="flex flex-1 flex-col">
      <div className="@container/main mt-8 flex flex-1 flex-col gap-14">
        <KPICards bookingKPIs={bookingKPIs} reviewKPIs={reviewKPIs} />

        <ChartBar
          title="Rating Distribution"
          yLabel=""
          yColor="var(--chart-3)"
          data={ratingDistribution.map((r) => ({
            x: r.rating.toString(),
            y: r.count,
          }))}
        />

        <BookingStatusChart data={bookingStatusDistribution} />

        <ChartLine
          title="Revenue by Month"
          yLabel="$"
          yColor="var(--chart-3)"
          data={revenueByMonth.map((r) => ({
            x: formatMonth(r.month),
            y: r.revenue,
          }))}
        />

        <ChartBar
          title="Bookings by Month"
          yLabel=""
          yColor="var(--chart-3)"
          data={bookingsByMonth.map((b) => ({
            x: formatMonth(b.month),
            y: b.bookings,
          }))}
        />
      </div>
    </div>
  );
}
