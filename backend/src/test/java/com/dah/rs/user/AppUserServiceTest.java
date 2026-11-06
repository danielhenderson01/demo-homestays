package com.dah.rs.user;

import static com.dah.rs.user.AppRole.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

public class AppUserServiceTest {

  @Mock
  private AppUserRepository userRepository;

  @InjectMocks
  private AppUserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getOrCreateAppUser_ShouldThrowException_WhenMissingSubject() {
    assertThrows(
      BadRequestException.class, () ->
        userService.getOrCreateAppUser(createInvalidToken(Claim.SUBJECT))
    );
  }

  @Test
  void getOrCreateAppUser_ShouldThrowException_WhenMissingName() {
    assertThrows(
      BadRequestException.class, () ->
        userService.getOrCreateAppUser(createInvalidToken(Claim.NAME))
    );
  }

  @Test
  void getOrCreateAppUser_ShouldThrowException_WhenMissingEmail() {
    assertThrows(
      BadRequestException.class, () ->
        userService.getOrCreateAppUser(createInvalidToken(Claim.EMAIL))
    );
  }

  @Test
  void getOrCreateAppUser_ShouldThrowException_WhenMissingRole() {
    assertThrows(
      BadRequestException.class, () ->
        userService.getOrCreateAppUser(createInvalidToken(Claim.ROLE))
    );
  }

  @Test
  void getOrCreateAppUser_ShouldCreateGuestAppUser_WhenNotFound() {
    AppUser guest = createUser(GUEST);
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.empty());

    when(userRepository.save(any(AppUser.class)))
      .thenReturn(guest);

    var result = userService.getOrCreateAppUser(guestJwt);

    assertNotNull(result);

    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void getOrCreateAppUser_ShouldCreateHostAppUser_WhenNotFound() {
    AppUser host = createUser(HOST);
    Jwt hostJwt = createJwt(HOST);
    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.empty());
    when(userRepository.save(any(AppUser.class)))
      .thenReturn(host);

    var result = userService.getOrCreateAppUser(hostJwt);

    assertNotNull(result);

    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void getHost_ShouldThrowException_WhenNotFound() {
    Jwt hostJwt = createJwt(HOST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () -> userService.getHost(hostJwt)
    );
  }

  @Test
  void getHost_ShouldThrowException_WhenInvalidRole() {
    AppUser guest = createUser(GUEST);
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.of(guest));

    assertThrows(
      AccessDeniedException.class, () -> userService.getHost(guestJwt)
    );
  }

  @Test
  void getHost_ShouldReturnHostAppUser() {
    AppUser host = createUser(HOST);
    Jwt hostJwt = createJwt(HOST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.of(host));

    var result = userService.getHost(hostJwt);

    assertNotNull(result);

    verify(userRepository).findByCognitoSub(anyString());
  }

  @Test
  void getGuest_ShouldThrowException_WhenNotFound() {
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () -> userService.getGuest(guestJwt)
    );
  }

  @Test
  void getGuest_ShouldThrowException_WhenInvalidRole() {
    AppUser host = createUser(HOST);
    Jwt hostJwt = createJwt(HOST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.of(host));

    assertThrows(
      AccessDeniedException.class, () -> userService.getGuest(hostJwt)
    );
  }

  @Test
  void getGuest_ShouldReturnGuestAppUser() {
    AppUser guest = createUser(GUEST);
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.of(guest));

    var result = userService.getGuest(guestJwt);

    assertNotNull(result);

    verify(userRepository).findByCognitoSub(anyString());
  }

  @Test
  void countUserResources_ShouldThrowException_WhenNotFound() {
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () -> userService.countUserResources(guestJwt)
    );
  }

  @Test
  void countUserResources_ShouldReturnUserResources() {
    AppUser guest = createUser(GUEST);
    Jwt guestJwt = createJwt(GUEST);

    when(userRepository.findByCognitoSub(anyString()))
      .thenReturn(Optional.of(guest));

    var result = userService.countUserResources(guestJwt);

    assertNotNull(result);
    assertEquals(0, result.numProperties());
    assertEquals(0, result.numReviews());
    assertEquals(0, result.numBookings());

    verify(userRepository).findByCognitoSub(anyString());
  }

  private enum Claim { SUBJECT, NAME, EMAIL, ROLE }

  private @NonNull Jwt createInvalidToken(Claim missingClaim) {
    Map<String, Object> invalidClaims = new HashMap<>();
    if (missingClaim != Claim.SUBJECT) invalidClaims.put("sub", "123456789");
    if (missingClaim != Claim.NAME) invalidClaims.put("name", "testing");
    if (missingClaim != Claim.EMAIL) invalidClaims.put("email", "testing@example.com");
    if (missingClaim != Claim.ROLE) invalidClaims.put("custom:role", "GUEST");
    return Jwt.withTokenValue("token")
      .header("alg", "none")
      .claims(existing -> existing.putAll(invalidClaims))
      .build();
  }

  private @NonNull Jwt createJwt(AppRole appRole) {
    switch (appRole) {
      case GUEST -> {
        Map<String, Object> guestClaims = new HashMap<>();
        guestClaims.put("sub", "123456789");
        guestClaims.put("name", "guest");
        guestClaims.put("email", "guest@example.com");
        guestClaims.put("custom:role", "GUEST");
        return Jwt.withTokenValue("token")
          .header("alg", "none")
          .claims(existing -> existing.putAll(guestClaims))
          .build();
      }
      case HOST -> {
        Map<String, Object> hostClaims = new HashMap<>();
        hostClaims.put("sub", "987654321");
        hostClaims.put("name", "host");
        hostClaims.put("email", "host@example.com");
        hostClaims.put("custom:role", "HOST");
        return Jwt.withTokenValue("token")
          .header("alg", "none")
          .claims(existing -> existing.putAll(hostClaims))
          .build();
      }
      case null, default ->
        throw new RuntimeException("Could not create JWT. Invalid AppRole provided.");
    }
  }

  private AppUser createUser(AppRole appRole) {
    switch (appRole) {
      case HOST -> {
        return AppUser.builder()
          .id(2L)
          .email("host@example.com")
          .role(HOST)
          .build();
      }
      case GUEST -> {
        return AppUser.builder()
          .id(1L)
          .email("guest@example.com")
          .role(GUEST)
          .build();
      }
      case null, default ->
        throw new RuntimeException("Could not create user. Invalid AppRole provided.");
    }
  }

}
