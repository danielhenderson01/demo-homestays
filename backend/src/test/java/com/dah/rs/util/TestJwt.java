package com.dah.rs.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@RequiredArgsConstructor
public class TestJwt {

  private final TestUsers testUsers;

  public JwtRequestPostProcessor hostJwt() {
    return jwt()
      .jwt(jwt -> jwt
        .subject(testUsers.hostSub())
        .claim("email", testUsers.hostEmail())
        .claim("name", testUsers.hostName())
        .claim("custom:role", "HOST")
      );
  }

  public JwtRequestPostProcessor guestJwt() {
    return jwt()
      .jwt(jwt -> jwt
        .subject(testUsers.guestSub())
        .claim("email", testUsers.guestEmail())
        .claim("name", testUsers.guestName())
        .claim("custom:role", "GUEST")
      );
  }

}
