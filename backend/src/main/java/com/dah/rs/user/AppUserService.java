package com.dah.rs.user;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AppUserService {

  private final AppUserRepository appUserRepository;

  public AppUser getOrCreateAppUser(Jwt jwt) {
    String subject = jwt.getSubject();

    if (subject == null) {
      throw new BadRequestException("Subject is required");
    }

    return appUserRepository
      .findByCognitoSub(subject)
      .orElseGet(() -> {
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        String role = jwt.getClaimAsString("custom:role");

        if (name == null || email == null || role == null) {
          throw new BadRequestException("Token is missing required claims");
        }

        AppUser appUser = AppUser.builder()
          .cognitoSub(subject)
          .name(name)
          .email(email)
          .role(role.equals("HOST") ? AppRole.HOST : AppRole.GUEST)
          .build();

        return appUserRepository.save(appUser);
      });
  }

  public AppUser getHost(Jwt jwt) {
    AppUser appUser = appUserRepository
      .findByCognitoSub(jwt.getSubject())
      .orElseThrow(() -> new NotFoundException("User not found"));

    if (appUser.getRole() != AppRole.HOST) {
      throw new AccessDeniedException("User role must be HOST");
    }

    return appUser;
  }

  public AppUser getGuest(Jwt jwt) {
    AppUser appUser = appUserRepository
      .findByCognitoSub(jwt.getSubject())
      .orElseThrow(() -> new NotFoundException("User not found"));

    if (appUser.getRole() != AppRole.GUEST) {
      throw new AccessDeniedException("User role must be GUEST");
    }

    return appUser;
  }

  public UserResources countUserResources(Jwt jwt) {
    return appUserRepository
      .findByCognitoSub(jwt.getSubject())
      .map(user -> new UserResources(
        user.getProperties().size(),
        user.getReviews().size(),
        user.getBookings().size()
      ))
      .orElseThrow(() -> new NotFoundException("User not found"));
  }

}
