package com.dah.rs.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @param propertyId Long
 * @param rating     Integer
 * @param comment    String
 */
public record ReviewRequest(
  @NotNull(message = "Property id is required")
  Long propertyId,
  @NotNull(message = "Rating is required")
  @Min(value = 1, message = "Rating cannot be less than 1")
  @Max(value = 5, message = "Rating cannot be more than 5")
  Integer rating,
  @NotBlank(message = "Comment is required")
  String comment
) {

}
