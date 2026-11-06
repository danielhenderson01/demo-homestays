package com.dah.rs.user;

/// @param id         Long
/// @param cognitoSub String
/// @param email      String
/// @param name       String
/// @param role       AppRole
public record UserResponse(
  Long id,
  String cognitoSub,
  String email,
  String name,
  AppRole role
) {

  public static UserResponse from(AppUser appUser) {
    return new UserResponse(
      appUser.getId(),
      appUser.getCognitoSub(),
      appUser.getEmail(),
      appUser.getName(),
      appUser.getRole()
    );
  }

}
