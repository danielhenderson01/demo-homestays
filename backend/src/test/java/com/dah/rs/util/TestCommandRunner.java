package com.dah.rs.util;

import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@RequiredArgsConstructor
public class TestCommandRunner implements CommandLineRunner {

  private final TestUsers testUsers;
  private final AppUserRepository appUserRepository;

  @Override
  public void run(String @NonNull ... args) {
    appUserRepository.save(
      AppUser.builder()
        .cognitoSub(testUsers.hostSub())
        .name(testUsers.hostName())
        .email(testUsers.hostEmail())
        .role(AppRole.HOST)
        .build()
    );

    appUserRepository.save(
      AppUser.builder()
        .cognitoSub(testUsers.guestSub())
        .name(testUsers.guestName())
        .email(testUsers.guestEmail())
        .role(AppRole.GUEST)
        .build()
    );
  }

}
