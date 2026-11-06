package com.dah.rs.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

  private final AppUserService appUserService;

  @GetMapping()
  public ResponseEntity<?> getOrCreateAppUser(
    @AuthenticationPrincipal Jwt jwt
  ) {
    AppUser appUser = appUserService.getOrCreateAppUser(jwt);
    UserResponse userResponse = UserResponse.from(appUser);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping("/resources")
  public ResponseEntity<?> countUserResources(
    @AuthenticationPrincipal Jwt jwt
  ) {
    return ResponseEntity.ok(appUserService.countUserResources(jwt));
  }

}
