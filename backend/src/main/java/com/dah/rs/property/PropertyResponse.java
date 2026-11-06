package com.dah.rs.property;

import com.dah.rs.image.PropertyImageResponse;
import com.dah.rs.review.RatingResponse;
import com.dah.rs.review.Review;
import com.dah.rs.review.ReviewResponse;
import com.dah.rs.user.HostResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @param id          Long
 * @param host        HostResponse
 * @param name        String
 * @param category    Category
 * @param description String
 * @param tagline     String
 * @param street      String
 * @param city        String
 * @param state       USState
 * @param zipcode     String
 * @param beds        int
 * @param baths       int
 * @param squareFeet  int
 * @param price       BigDecimal
 * @param amenities   Set
 * @param images      List
 * @param reviews     List
 * @param rating      RatingResponse
 */
public record PropertyResponse(
  Long id,
  HostResponse host,
  String name,
  Category category,
  String description,
  String tagline,
  String street,
  String city,
  USState state,
  String zipcode,
  int beds,
  int baths,
  int squareFeet,
  BigDecimal price,
  Set<Amenity> amenities,
  List<PropertyImageResponse> images,
  List<ReviewResponse> reviews,
  RatingResponse rating
) {

  public static PropertyResponse from(Property property) {
    List<Review> propertyReviews = new ArrayList<>();
    if (property.getReviews() != null) {
      propertyReviews.addAll(property.getReviews());
    }
    return new PropertyResponse(
      property.getId(),
      HostResponse.from(property.getHost()),
      property.getName(),
      property.getCategory(),
      property.getDescription(),
      property.getTagline(),
      property.getStreet(),
      property.getCity(),
      property.getState(),
      property.getZipcode(),
      property.getBeds(),
      property.getBaths(),
      property.getSquareFeet(),
      property.getPrice(),
      property.getAmenities(),
      property.getImages().stream().map(PropertyImageResponse::from).toList(),
      propertyReviews.stream().map(ReviewResponse::from).toList(),
      RatingResponse.fromReviews(propertyReviews)
    );
  }

}

