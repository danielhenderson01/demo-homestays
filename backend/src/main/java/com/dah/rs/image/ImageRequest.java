package com.dah.rs.image;

import jakarta.validation.constraints.NotBlank;

///
/// @param objectKey String
public record ImageRequest(
  @NotBlank String objectKey
) {

}
