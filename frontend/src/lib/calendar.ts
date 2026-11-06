import type { DateRange } from "react-day-picker";
import type { BookingDates } from "./types";

export const defaultSelected: DateRange = {
  from: undefined,
  to: undefined,
};

export function getBlockedDates({
  bookings,
  today,
}: {
  bookings: BookingDates[];
  today: Date;
}) {
  if (!bookings || bookings.length < 1) {
    return [
      {
        from: undefined,
        to: undefined,
      },
      {
        from: new Date(0),
        to: new Date(today.getTime() - 24 * 60 * 60 * 1000),
      },
    ];
  }
  today.setHours(0, 0, 0, 0);
  const blockedDates: DateRange[] = [
    ...bookings.map((booking) => ({
      from: new Date(booking.checkIn),
      to: new Date(booking.checkOut),
    })),
    {
      from: new Date(0),
      to: new Date(today.getTime() - 24 * 60 * 60 * 1000),
    },
  ];
  return blockedDates;
}

export function getDateRange(range: DateRange | undefined): string[] {
  if (!range || !range.from || !range.to) return [];
  const currentDate = new Date(range.from);
  const endDate = new Date(range.to);
  const dateRange: string[] = [];
  while (currentDate <= endDate) {
    const dateString = currentDate.toISOString().split("T")[0];
    dateRange.push(dateString);
    currentDate.setDate(currentDate.getDate() + 1);
  }
  return dateRange;
}

export function getDisabledDates(disabledDays: DateRange[]): {
  [key: string]: boolean;
} {
  if (disabledDays.length === 0) return {};
  const disabledDates: { [key: string]: boolean } = {};
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  disabledDays.forEach((range) => {
    if (!range.from || !range.to) return;
    const currentDate = new Date(range.from);
    const endDate = new Date(range.to);
    while (currentDate <= endDate) {
      if (currentDate < today) {
        currentDate.setDate(currentDate.getDate() + 1);
        continue;
      }
      const dateString = currentDate.toISOString().split("T")[0];
      disabledDates[dateString] = true;
      currentDate.setDate(currentDate.getDate() + 1);
    }
  });
  return disabledDates;
}

export function getDaysBetween({
  checkIn,
  checkOut,
}: {
  checkIn?: Date;
  checkOut?: Date;
}) {
  if (!checkIn || !checkOut) return 0;
  const diffInMs = Math.abs(checkOut.getTime() - checkIn.getTime());
  return diffInMs / (1000 * 60 * 60 * 24);
}

export function calculateBookingPrice({
  checkIn,
  checkOut,
  price,
}: {
  checkIn: Date;
  checkOut: Date;
  price: number;
}) {
  const totalNights = getDaysBetween({ checkIn, checkOut });
  const orderTotal = totalNights * price;
  return {
    totalNights,
    orderTotal: orderTotal,
  };
}
