package com.dah.rs.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @param hostSub    String
 * @param hostEmail  String
 * @param hostName   String
 * @param guestSub   String
 * @param guestEmail String
 * @param guestName  String
 */
@Profile("test")
@Component
public record TestUsers(
  @Value("${hs.app.host-sub}") String hostSub,
  @Value("${hs.app.host-email}") String hostEmail,
  @Value("${hs.app.host-name}") String hostName,
  @Value("${hs.app.guest-sub}") String guestSub,
  @Value("${hs.app.guest-email}") String guestEmail,
  @Value("${hs.app.guest-name}") String guestName
) {

}
