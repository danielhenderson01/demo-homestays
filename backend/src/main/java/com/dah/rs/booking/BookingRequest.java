package com.dah.rs.booking;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * @param propertyId Long
 * @param checkIn    LocalDate
 * @param checkOut   LocalDate
 */
public record BookingRequest(
  @NotNull(message = "Property ID is required")
  Long propertyId,
  @NotNull(message = "Check-in is required")
  LocalDate checkIn,
  @NotNull(message = "Check-out is required")
  LocalDate checkOut
) {

}
