package com.dah.rs.review;

import com.dah.rs.user.GuestResponse;

/**
 * @param id            Long
 * @param propertyId    Long
 * @param guest         GuestResponse
 * @param propertyName  String
 * @param rating        Integer
 * @param comment       String
 * @param propertyImage String
 */
public record ReviewResponse(
  Long id,
  Long propertyId,
  GuestResponse guest,
  String propertyName,
  Integer rating,
  String comment,
  String propertyImage
) {

  public static ReviewResponse from(Review review) {
    return new ReviewResponse(
      review.getId(),
      review.getProperty().getId(),
      GuestResponse.from(review.getGuest()),
      review.getProperty().getName(),
      review.getRating(),
      review.getComment(),
      review.getProperty().getImages().isEmpty()
        ? "/images/a.jpg"
        : review.getProperty().getImages().getFirst().getImageUrl()
    );
  }

}
