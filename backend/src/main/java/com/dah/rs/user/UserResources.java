package com.dah.rs.user;

/**
 * @param numProperties Integer
 * @param numReviews    Integer
 * @param numBookings   Integer
 */
public record UserResources(
  Integer numProperties,
  Integer numReviews,
  Integer numBookings
) {

}
