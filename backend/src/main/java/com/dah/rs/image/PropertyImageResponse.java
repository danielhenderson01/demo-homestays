package com.dah.rs.image;

/**
 * @param id         Long
 * @param propertyId Long
 * @param imageUrl   String
 * @param objectKey  String
 */
public record PropertyImageResponse(
  Long id,
  Long propertyId,
  String imageUrl,
  String objectKey
) {

  public static PropertyImageResponse from(PropertyImage image) {
    return new PropertyImageResponse(
      image.getId(),
      image.getProperty().getId(),
      image.getImageUrl(),
      image.getObjectKey()
    );
  }

}
