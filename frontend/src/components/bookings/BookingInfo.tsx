import {
  CircleDollarSign,
  DoorClosed,
  DoorOpen,
  MapPin,
  MoonStar,
} from "lucide-react";
import type { Booking } from "@/lib/types";
import { IMAGES_URL } from "@/lib/constants";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemMedia,
  ItemTitle,
} from "../ui/item";
import BookingStatusBadge from "../common/BookingStatusBadge";
import { Separator } from "../ui/separator";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { formatCurrency } from "@/lib/formatters";

export default function BookingInfo({ booking }: { booking: Booking }) {
  const { street, city, state, zipcode } = booking.address;

  return (
    <div className="rounded-lg border pb-2">
      <div className="mb-1 overflow-hidden rounded-t-lg">
        <img
          src={booking.propertyImages[0] ?? `${IMAGES_URL}/samples/a.jpg`}
          alt={booking.propertyName}
          className="aspect-video object-cover"
        />
      </div>

      <div className="flex flex-col">
        <Item size="xs">
          <ItemMedia>
            <Avatar>
              <AvatarImage src={`${IMAGES_URL}/samples/profile-default.jpg`} />
              <AvatarFallback>{booking.host.name.charAt(0)}</AvatarFallback>
            </Avatar>
          </ItemMedia>
          <ItemContent>
            <ItemTitle>Hosted by {booking.host.name}</ItemTitle>
            <ItemDescription className="text-xs font-medium">
              {booking.host.email}
            </ItemDescription>
          </ItemContent>
          <ItemActions>
            <BookingStatusBadge status={booking.bookingStatus} />
          </ItemActions>
        </Item>

        <Separator className="my-1" />

        <div className="px-2">
          <Item>
            <ItemMedia variant="icon">
              <MapPin />
            </ItemMedia>
            <ItemContent>
              <ItemTitle>Address</ItemTitle>
              <ItemDescription>
                {street}, {city}, {state} {zipcode}
              </ItemDescription>
            </ItemContent>
          </Item>

          <div className="flex items-center justify-center">
            <Item>
              <ItemMedia variant="icon">
                <DoorOpen />
              </ItemMedia>
              <ItemContent>
                <ItemTitle>Check In</ItemTitle>
                <ItemDescription>{booking.checkIn}</ItemDescription>
              </ItemContent>
            </Item>
            <Item>
              <ItemMedia variant="icon">
                <DoorClosed />
              </ItemMedia>
              <ItemContent>
                <ItemTitle>Check Out</ItemTitle>
                <ItemDescription>{booking.checkOut}</ItemDescription>
              </ItemContent>
            </Item>
          </div>

          <div className="flex items-center justify-center">
            <Item>
              <ItemMedia variant="icon">
                <MoonStar />
              </ItemMedia>
              <ItemContent>
                <ItemTitle>{booking.totalNights} nights</ItemTitle>
                <ItemDescription className="tabular-nums">
                  {formatCurrency(booking.pricePerNight)} / night
                </ItemDescription>
              </ItemContent>
            </Item>
            <Item>
              <ItemMedia variant="icon">
                <CircleDollarSign />
              </ItemMedia>
              <ItemContent>
                <ItemTitle>Order Total</ItemTitle>
                <ItemDescription className="tabular-nums">
                  {formatCurrency(booking.orderTotal)}
                </ItemDescription>
              </ItemContent>
            </Item>
          </div>
        </div>
      </div>
    </div>
  );
}
