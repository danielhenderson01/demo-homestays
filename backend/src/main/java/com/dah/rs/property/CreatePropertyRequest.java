package com.dah.rs.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public record CreatePropertyRequest(
  @NotBlank(message = "Name is required")
  String name,
  @NotNull(message = "Category is required")
  Category category,
  @NotBlank(message = "Description is required")
  String description,
  @NotBlank(message = "Tagline is required")
  String tagline,
  @NotBlank(message = "Street is required")
  String street,
  @NotBlank(message = "City is required")
  String city,
  @NotNull(message = "State is required")
  USState state,
  @NotBlank(message = "Zipcode is required")
  String zipcode,
  @NotNull(message = "Beds is required")
  @PositiveOrZero(message = "Beds must be positive or zero")
  Integer beds,
  @NotNull(message = "Baths is required")
  @PositiveOrZero(message = "Baths must be positive or zero")
  Integer baths,
  @NotNull(message = "Square feet is required")
  @Positive(message = "Square feet must be positive")
  Integer squareFeet,
  @NotNull(message = "Price is required")
  @PositiveOrZero(message = "Price must be positive or zero")
  BigDecimal price,
  @NotNull(message = "Amenities is required")
  Set<Amenity> amenities
) {

}
