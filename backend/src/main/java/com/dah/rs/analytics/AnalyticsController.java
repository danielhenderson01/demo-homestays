package com.dah.rs.analytics;

import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

  private final AnalyticsService analyticsService;
  private final AppUserService appUserService;

  @GetMapping()
  public ResponseEntity<?> getHostDashboard(@AuthenticationPrincipal Jwt jwt) {
    AppUser host = appUserService.getHost(jwt);
    return ResponseEntity.ok(analyticsService.getDashboard(host));
  }

}
