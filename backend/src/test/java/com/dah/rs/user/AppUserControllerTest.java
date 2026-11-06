package com.dah.rs.user;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dah.rs.config.SecurityConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppUserController.class)
@Import(SecurityConfig.class)
class AppUserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AppUserService appUserService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  private Jwt testJwt;

  @BeforeEach
  void setUp() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "123456789");
    claims.put("name", "Jane");
    claims.put("email", "jane@example.com");
    claims.put("custom:role", "GUEST");
    testJwt = Jwt.withTokenValue("token")
      .header("alg", "none")
      .claims(existing -> existing.putAll(claims))
      .build();
  }

  @Test
  void getOrCreateAppUser_WithoutAuthentication_Returns401() throws Exception {
    mockMvc
      .perform(get("/api/users"))
      .andExpect(status().isUnauthorized());

    then(appUserService).shouldHaveNoInteractions();
  }

  @Test
  void countUserResources_WithoutAuthentication_Returns401() throws Exception {
    mockMvc
      .perform(get("/api/users/resources"))
      .andExpect(status().isUnauthorized());

    then(appUserService).shouldHaveNoInteractions();
  }

  @Test
  void getOrCreateAppUser_Returns200() throws Exception {
    AppUser guest = AppUser.builder()
      .id(1L)
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();

    given(appUserService.getOrCreateAppUser(testJwt))
      .willReturn(guest);

    mockMvc
      .perform(get("/api/users").with(jwt()))
      .andExpect(status().isOk());

    then(appUserService)
      .should()
      .getOrCreateAppUser(testJwt);
  }

  @Test
  void countUserResources_Returns200() throws Exception {
    given(appUserService.countUserResources(testJwt))
      .willReturn(new UserResources(0, 5, 5));

    mockMvc
      .perform(get("/api/users/resources").with(jwt()))
      .andExpect(status().isOk());

    then(appUserService)
      .should()
      .countUserResources(testJwt);
  }

}