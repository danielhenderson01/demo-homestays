package com.dah.rs.analytics;

import java.time.Instant;

/// @param month    Instant
/// @param bookings Long
public record BookingsByMonth(
  Instant month,
  Long bookings
) {

}
