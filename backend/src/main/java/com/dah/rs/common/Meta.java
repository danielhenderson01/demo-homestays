package com.dah.rs.common;

/// @param number        int
/// @param size          int
/// @param totalElements long
/// @param totalPages    int
/// @param first         boolean
/// @param last          boolean
public record Meta(
  int number,
  int size,
  long totalElements,
  int totalPages,
  boolean first,
  boolean last
) {

}
