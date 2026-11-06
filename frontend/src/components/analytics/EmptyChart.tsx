import { Activity, ChartColumn, ChartLine, ChartPie } from "lucide-react";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "../ui/empty";
import { Button } from "../ui/button";
import { Link } from "react-router";
import { HOST_PROPERTIES_PATH } from "@/lib/constants";

export default function EmptyChart({
  kind,
}: {
  kind?: "bar" | "line" | "pie";
}) {
  return (
    <Empty>
      <EmptyHeader>
        <EmptyMedia variant="icon">
          {kind === "bar" ? (
            <ChartColumn />
          ) : kind === "line" ? (
            <ChartLine />
          ) : kind === "pie" ? (
            <ChartPie />
          ) : (
            <Activity />
          )}
        </EmptyMedia>
        <EmptyTitle>No Data</EmptyTitle>
        <EmptyDescription>
          Nothing to see here until one of your properties gets booked or
          someone leaves a review.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button variant="outline" size="sm" asChild>
          <Link to={HOST_PROPERTIES_PATH}>View Properties</Link>
        </Button>
      </EmptyContent>
    </Empty>
  );
}
