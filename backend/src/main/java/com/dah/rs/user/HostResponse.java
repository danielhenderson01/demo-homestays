package com.dah.rs.user;

/// @param id    Long
/// @param name  String
/// @param email String
public record HostResponse(
  Long id,
  String name,
  String email
) {

  public static HostResponse from(AppUser appUser) {
    return new HostResponse(
      appUser.getId(),
      appUser.getName(),
      appUser.getEmail()
    );
  }

}
