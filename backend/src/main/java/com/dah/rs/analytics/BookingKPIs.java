package com.dah.rs.analytics;

import java.math.BigDecimal;

/// @param totalBookings       Long
/// @param totalRevenue        BigDecimal
/// @param bookedNights        Long
/// @param averageBookingValue BigDecimal
public record BookingKPIs(
  Long totalBookings,
  BigDecimal totalRevenue,
  Long bookedNights,
  BigDecimal averageBookingValue
) {

}
