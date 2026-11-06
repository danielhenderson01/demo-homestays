package com.dah.rs.exception;

/**
 * @param detail   String
 * @param instance String
 * @param status   int
 * @param title    String
 */
public record ApiError(
  String detail,
  String instance,
  int status,
  String title
) {

  public static ApiError of(
    String detail,
    String instance,
    int status,
    String title
  ) {
    return new ApiError(detail, instance, status, title);
  }

}
