package com.dah.rs.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// @param month         LocalDateTime
/// @param averageRating BigDecimal
/// @param reviewCount   Long
public record AverageRatingOverTime(
  LocalDateTime month,
  BigDecimal averageRating,
  Long reviewCount
) {
}
