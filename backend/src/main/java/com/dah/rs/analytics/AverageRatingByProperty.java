package com.dah.rs.analytics;

import java.math.BigDecimal;

/// @param id            Long
/// @param name          String
/// @param averageRating BigDecimal
/// @param reviewCount   Long
public record AverageRatingByProperty(
  Long id,
  String name,
  BigDecimal averageRating,
  Long reviewCount
) {
}
