package com.dah.rs.review;

import java.util.List;

/**
 * @param averageRating double
 * @param numRatings    int
 */
public record RatingResponse(
  double averageRating,
  int numRatings
) {

  public static RatingResponse fromReviews(List<Review> reviews) {
    return reviews.isEmpty()
      ? new RatingResponse(0.0, 0)
      : new RatingResponse(
        reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0),
        reviews.size()
      );
  }

}