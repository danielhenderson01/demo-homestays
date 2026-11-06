package com.dah.rs.user;

/// @param id    Long
/// @param name  String
/// @param email String
public record GuestResponse(
  Long id,
  String name,
  String email
) {

  public static GuestResponse from(AppUser appUser) {
    return new GuestResponse(
      appUser.getId(),
      appUser.getName(),
      appUser.getEmail()
    );
  }

}
