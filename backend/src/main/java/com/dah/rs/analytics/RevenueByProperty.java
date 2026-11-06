package com.dah.rs.analytics;

import java.math.BigDecimal;

/// @param id           Long
/// @param name         String
/// @param bookingCount Long
/// @param revenue      BigDecimal
public record RevenueByProperty(
  Long id,
  String name,
  Long bookingCount,
  BigDecimal revenue
) {

}
