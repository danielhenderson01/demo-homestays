package com.dah.rs.analytics;

/// @param bookingStatus String
/// @param count         Long
public record BookingStatusDistribution(
  String bookingStatus,
  Long count
) {

}
