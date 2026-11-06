package com.dah.rs.booking;

import jakarta.validation.constraints.NotNull;

/**
 * @param id            Long
 * @param bookingStatus BookingStatus
 */
public record BookingStatusRequest(
  @NotNull(message = "ID is required")
  Long id,
  @NotNull(message = "Booking status is required")
  BookingStatus bookingStatus
) {

}
