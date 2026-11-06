import { Activity, Calendar, House, MessageCircle } from "lucide-react";
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

export default function EmptyResource({
  kind,
}: {
  kind: "booking" | "review" | "property";
}) {
  return (
    <Empty>
      {kind === "booking" ? (
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <Calendar />
          </EmptyMedia>
          <EmptyTitle>No Bookings</EmptyTitle>
          <EmptyDescription>
            Nothing to see here until one of your properties is booked.
          </EmptyDescription>
        </EmptyHeader>
      ) : kind === "review" ? (
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <MessageCircle />
          </EmptyMedia>
          <EmptyTitle>No Reviews</EmptyTitle>
          <EmptyDescription>
            Nothing to see here until someone reviews one of your properties.
          </EmptyDescription>
        </EmptyHeader>
      ) : kind === "property" ? (
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <House />
          </EmptyMedia>
          <EmptyTitle>No Properties</EmptyTitle>
          <EmptyDescription>
            Nothing to see here until you list a property.
          </EmptyDescription>
        </EmptyHeader>
      ) : (
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <Activity />
          </EmptyMedia>
          <EmptyTitle>No Data</EmptyTitle>
          <EmptyDescription>Nothing to see here...</EmptyDescription>
        </EmptyHeader>
      )}
      <EmptyContent>
        <Button variant="outline" size="sm" asChild>
          <Link to={HOST_PROPERTIES_PATH}>View Properties</Link>
        </Button>
      </EmptyContent>
    </Empty>
  );
}
