import { Calendar } from "@/components/ui/calendar";
import { Card, CardContent } from "@/components/ui/card";
import {
  getBlockedDates,
  getDateRange,
  getDisabledDates,
} from "@/lib/calendar";
import { useEffect, useState } from "react";
import { formatCurrency } from "@/lib/formatters";
import type { DateRange } from "react-day-picker";
import { calculateBookingPrice } from "@/lib/calendar";
import { useFetcher, useLoaderData } from "react-router";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "../ui/button";
import { Book, Loader } from "lucide-react";
import type { loader } from "@/router/routes/guest/GuestProperty";
import { useSuspenseQuery } from "@tanstack/react-query";
import { bookingInfoDetailQuery } from "@/api/bookings";
import TooltipButton from "../common/TooltipButton";

export default function BookingForm() {
  const { propertyId } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: bookingInfo } = useSuspenseQuery(
    bookingInfoDetailQuery(propertyId)
  );

  const fetcher = useFetcher();

  const { bookingDates, propertyPrice } = bookingInfo;

  const [range, setRange] = useState<DateRange | undefined>({
    from: undefined,
    to: undefined,
  });

  const currentDate = new Date();

  const blockedPeriods = getBlockedDates({
    bookings: bookingDates,
    today: currentDate,
  });

  const unavailableDates = getDisabledDates(blockedPeriods);

  useEffect(() => {
    const selectedRange = getDateRange(range);
    selectedRange.some((date) => {
      if (unavailableDates[date]) {
        setRange({ from: undefined, to: undefined });
        return true;
      }
      return false;
    });
  }, [range, unavailableDates]);

  const checkIn = range?.from as Date;
  const checkOut = range?.to as Date;

  const { totalNights, orderTotal } = calculateBookingPrice({
    checkIn,
    checkOut,
    price: propertyPrice,
  });

  const isInvalidRange =
    !range ||
    !range.from ||
    !range.to ||
    range.to.getTime() === range.from.getTime();

  return (
    <Dialog>
      <DialogTrigger asChild>
        <TooltipButton tooltipText="Book Now">
          <Book className="size-4" />
          <span className="hidden xl:flex">Book</span>
        </TooltipButton>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>When do you plan on staying?</DialogTitle>
        </DialogHeader>
        <Card className="mx-auto flex w-full max-w-lg flex-col bg-background">
          <CardContent className="mx-auto flex min-h-100 w-full max-w-md flex-col justify-between p-4">
            <div className="flex flex-col items-center">
              <div className="min-h-75 p-0">
                <Calendar
                  mode="range"
                  defaultMonth={currentDate}
                  selected={range}
                  onSelect={setRange}
                  disabled={blockedPeriods}
                />
              </div>
            </div>
            {isInvalidRange ? (
              <div className="flex flex-col gap-4 p-2">
                <p className="flex justify-between">
                  <span>$0 x 0 nights</span>
                  <span>{formatCurrency(0)}</span>
                </p>
                <p className="flex justify-between">
                  <span>Booking Total</span>
                  <span>{formatCurrency(0)}</span>
                </p>
                <Button disabled className="w-full">
                  Submit
                </Button>
              </div>
            ) : (
              <div className="flex flex-col gap-4 p-2">
                <p className="flex justify-between">
                  <span>{`$${propertyPrice} x ${totalNights} nights`}</span>
                  <span>{formatCurrency(orderTotal)}</span>
                </p>
                <p className="flex justify-between">
                  <span>Booking Total</span>
                  <span>{formatCurrency(orderTotal)}</span>
                </p>
                <fetcher.Form method="post">
                  <input
                    type="hidden"
                    id="formId"
                    name="formId"
                    defaultValue="booking-form"
                  />
                  <input
                    type="hidden"
                    id="propertyId"
                    name="propertyId"
                    value={propertyId}
                  />
                  <input
                    type="hidden"
                    id="checkIn"
                    name="checkIn"
                    value={checkIn.toISOString()}
                  />
                  <input
                    type="hidden"
                    id="checkOut"
                    name="checkOut"
                    value={checkOut.toISOString()}
                  />
                  <Button
                    type="submit"
                    className="w-full"
                    disabled={fetcher.state !== "idle"}
                  >
                    {fetcher.state !== "idle" ? (
                      <Loader className="size-4 animate-spin" />
                    ) : (
                      <span>Submit</span>
                    )}
                  </Button>
                </fetcher.Form>
              </div>
            )}
          </CardContent>
        </Card>
      </DialogContent>
    </Dialog>
  );
}
