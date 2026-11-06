import { CartesianGrid, LabelList, Line, LineChart, XAxis } from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart";
import EmptyChart from "./EmptyChart";

export default function ChartLine({
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
        <EmptyChart kind="line" />
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
              <LineChart
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
                  tickLine={true}
                  axisLine={false}
                  tickMargin={8}
                  tickFormatter={(value) => value.slice(0, 3)}
                />
                <ChartTooltip
                  cursor={false}
                  content={<ChartTooltipContent hideLabel hideIndicator />}
                  defaultIndex={1}
                />
                <Line
                  dataKey="y"
                  type="natural"
                  stroke="var(--color-y)"
                  strokeWidth={2}
                  dot={{
                    fill: "var(--color-y)",
                  }}
                  activeDot={{
                    r: 6,
                  }}
                >
                  <LabelList
                    position="top"
                    offset={12}
                    className="fill-foreground"
                    fontSize={12}
                  />
                </Line>
              </LineChart>
            </ChartContainer>
          </CardContent>
        </>
      )}
    </Card>
  );
}
