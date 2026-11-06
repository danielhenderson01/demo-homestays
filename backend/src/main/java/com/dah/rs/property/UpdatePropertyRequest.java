package com.dah.rs.property;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @param name        String
 * @param category    Category
 * @param description String
 * @param tagline     String
 * @param street      String
 * @param city        String
 * @param state       USState
 * @param zipcode     String
 * @param beds        Integer
 * @param baths       Integer
 * @param squareFeet  Integer
 * @param price       BigDecimal
 * @param amenities   Set
 */
public record UpdatePropertyRequest(
  Long id,
  String name,
  Category category,
  String description,
  String tagline,
  String street,
  String city,
  USState state,
  String zipcode,
  @PositiveOrZero(message = "Beds must be positive or zero")
  Integer beds,
  @PositiveOrZero(message = "Baths must be positive or zero")
  Integer baths,
  @Positive(message = "Square feet must be positive")
  Integer squareFeet,
  @PositiveOrZero(message = "Price must be positive or zero")
  BigDecimal price,
  Set<Amenity> amenities
) {

}
