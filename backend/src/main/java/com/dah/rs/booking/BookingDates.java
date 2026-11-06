package com.dah.rs.booking;

/**
 * @param checkIn  String
 * @param checkOut String
 */
public record BookingDates(
  String checkIn,
  String checkOut
) {

  public static BookingDates from(Booking booking) {
    return new BookingDates(
      booking.getCheckIn().toString() + "T00:00:00",
      booking.getCheckOut().toString() + "T00:00:00"
    );
  }

}
