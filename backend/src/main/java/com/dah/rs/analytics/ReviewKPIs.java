package com.dah.rs.analytics;

import java.math.BigDecimal;

/// @param totalReviews  Long
/// @param averageRating BigDecimal
public record ReviewKPIs(
  Long totalReviews,
  BigDecimal averageRating
) {
}
