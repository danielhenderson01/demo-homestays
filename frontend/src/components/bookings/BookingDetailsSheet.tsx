import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Button } from "../ui/button";
import type { Booking } from "@/lib/types";
import { Separator } from "../ui/separator";
import { formatCurrency } from "@/lib/formatters";
import { Eye } from "lucide-react";
import { Card } from "../ui/card";
import BookingStatusBadge from "../common/BookingStatusBadge";

export default function BookingDetailsSheet({ booking }: { booking: Booking }) {
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button size="icon-sm" variant="ghost">
          <Eye className="text-indigo-500" />
        </Button>
      </SheetTrigger>

      <SheetContent>
        <SheetHeader>
          <SheetTitle className="text-center">Booking Details</SheetTitle>
        </SheetHeader>

        <div className="flex flex-col gap-4 px-6">
          {/* --- Image --- */}
          <Card className="mb-2 flex aspect-video overflow-hidden">
            <img
              src={booking.propertyImages[0]}
              alt="property image"
              className="w-full object-cover"
            />
          </Card>

          {/* --- Property --- */}
          <div className="flex items-center justify-between">
            <span>Property:</span>
            <div className="flex flex-col gap-1 text-right">
              <span className="truncate">{booking.propertyName}</span>
            </div>
          </div>
          <Separator />

          {/* --- Guest --- */}
          <div className="flex justify-between">
            <span>Guest:</span>
            <div className="flex flex-col gap-1 text-right">
              <span className="truncate">{booking.guest.name}</span>
              <span className="truncate text-blue-500">
                <a href={`mailto:${booking.guest.email}`}>
                  {booking.guest.email}
                </a>
              </span>
            </div>
          </div>
          <Separator />

          {/* --- Nights --- */}
          <div className="flex justify-between">
            <span>Nights:</span>
            <span className="text-right">{booking.totalNights} nights</span>
          </div>
          {/* --- CheckIn --- */}
          <div className="ml-4 flex justify-between">
            <span className="text-xs">Check in:</span>
            <span className="text-right">{booking.checkIn}</span>
          </div>
          {/* --- CheckOut --- */}
          <div className="ml-4 flex justify-between">
            <span className="text-xs">Check out:</span>
            <span className="text-right">{booking.checkOut}</span>
          </div>
          <Separator />

          {/* --- Price --- */}
          <div className="flex justify-between">
            <span>Price:</span>
            <div className="flex flex-col text-right">
              <span>{formatCurrency(booking.orderTotal)}</span>
            </div>
          </div>
          <Separator />

          {/* --- Status --- */}
          <div className="flex justify-between">
            <span>Status:</span>
            <span>
              <BookingStatusBadge status={booking.bookingStatus} />
            </span>
          </div>
          <Separator />
        </div>
      </SheetContent>
    </Sheet>
  );
}
