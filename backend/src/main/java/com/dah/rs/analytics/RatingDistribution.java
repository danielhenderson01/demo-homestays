package com.dah.rs.analytics;

/// @param rating Integer
/// @param count  Long
public record RatingDistribution(
  Integer rating,
  Long count
) {
}
