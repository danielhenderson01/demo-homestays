import { Pie, PieChart } from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  type ChartConfig,
} from "@/components/ui/chart";
import type { BookingStatusDistribution } from "@/lib/types";
import EmptyChart from "./EmptyChart";

const chartConfig = {
  count: {
    label: "Count",
  },
  CONFIRMED: {
    label: "CONFIRMED",
    color: "var(--chart-2)",
  },
  PENDING: {
    label: "PENDING",
    color: "var(--chart-4)",
  },
  CANCELLED: {
    label: "CANCELLED",
    color: "var(--chart-5)",
  },
} satisfies ChartConfig;

export default function BookingStatusChart({
  data,
}: {
  data: BookingStatusDistribution[];
}) {
  return (
    <Card className="flex flex-col shadow-lg dark:bg-background">
      {data.length < 1 ? (
        <EmptyChart kind="pie" />
      ) : (
        <>
          <CardHeader className="items-center pb-0">
            <CardTitle>Booking Status Distribution</CardTitle>
          </CardHeader>
          <CardContent className="flex-1 pb-0">
            <ChartContainer
              config={chartConfig}
              className="mx-auto aspect-square max-h-75"
            >
              <PieChart>
                <Pie
                  label
                  labelLine={false}
                  data={data.map((d) => ({
                    ...d,
                    fill: `var(--color-${d.bookingStatus})`,
                  }))}
                  dataKey="count"
                  nameKey="bookingStatus"
                  innerRadius={60}
                />
                <ChartLegend
                  content={<ChartLegendContent nameKey="bookingStatus" />}
                />
              </PieChart>
            </ChartContainer>
          </CardContent>
        </>
      )}
    </Card>
  );
}
