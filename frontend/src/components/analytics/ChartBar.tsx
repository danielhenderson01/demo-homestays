import { Bar, BarChart, CartesianGrid, LabelList, XAxis } from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ChartContainer, type ChartConfig } from "@/components/ui/chart";
import EmptyChart from "./EmptyChart";

export function ChartBar({
  title,
  yLabel,
  yColor,
  data,
}: {
  title: string;
  yLabel: string;
  yColor?: string;
  data: { x: string; y: number }[];
}) {
  return (
    <Card className="shadow-lg dark:bg-background">
      {data.length < 1 ? (
        <EmptyChart kind="bar" />
      ) : (
        <>
          <CardHeader>
            <CardTitle>{title}</CardTitle>
          </CardHeader>
          <CardContent className="px-2 pt-4 sm:px-6 sm:pt-6">
            <ChartContainer
              config={
                {
                  y: {
                    label: yLabel,
                    color: yColor || "var(--chart-1)",
                  },
                } satisfies ChartConfig
              }
              className="aspect-auto h-62 w-full"
            >
              <BarChart
                accessibilityLayer
                data={data}
                margin={{
                  top: 30,
                  left: 30,
                  right: 30,
                }}
              >
                <CartesianGrid vertical={false} />
                <XAxis
                  dataKey="x"
                  tickLine={false}
                  tickMargin={10}
                  axisLine={false}
                  tickFormatter={(value) => value.slice(0, 3)}
                />
                <Bar dataKey="y" fill="var(--color-y)" radius={8}>
                  <LabelList
                    position="top"
                    offset={12}
                    className="fill-foreground"
                    fontSize={12}
                  />
                </Bar>
              </BarChart>
            </ChartContainer>
          </CardContent>
        </>
      )}
    </Card>
  );
}
