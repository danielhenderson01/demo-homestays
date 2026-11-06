import { Badge } from "../ui/badge";

export default function BookingStatusBadge({ status }: { status: string }) {
  return (
    <Badge
      className={`w-full ${
        status === "CANCELLED"
          ? "bg-red-50 text-red-700 dark:bg-red-950 dark:text-red-300"
          : status === "CONFIRMED"
            ? "bg-green-50 text-green-700 dark:bg-green-950 dark:text-green-300"
            : ""
      }`}
    >
      {status}
    </Badge>
  );
}
