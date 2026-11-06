package com.dah.rs.analytics;

import java.math.BigDecimal;

/// @param minimum       BigDecimal
/// @param firstQuartile BigDecimal
/// @param median        BigDecimal
/// @param thirdQuartile BigDecimal
/// @param maximum       BigDecimal
public record FiveNumberSummary(
  BigDecimal minimum,
  BigDecimal firstQuartile,
  BigDecimal median,
  BigDecimal thirdQuartile,
  BigDecimal maximum
) {

  public static FiveNumberSummary ofZero() {
    return new FiveNumberSummary(
      BigDecimal.ZERO,
      BigDecimal.ZERO,
      BigDecimal.ZERO,
      BigDecimal.ZERO,
      BigDecimal.ZERO
    );
  }

}
