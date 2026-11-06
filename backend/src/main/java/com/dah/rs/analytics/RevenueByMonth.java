package com.dah.rs.analytics;

import java.math.BigDecimal;
import java.time.Instant;

/// @param month   Instant
/// @param revenue BigDecimal
public record RevenueByMonth(
  Instant month,
  BigDecimal revenue
) {

}
