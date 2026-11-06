package com.dah.rs.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dah.rs.config.SecurityConfig;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnalyticsController.class)
@Import(SecurityConfig.class)
public class AnalyticsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AnalyticsService analyticsService;

  @MockitoBean
  private AppUserService appUserService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @Test
  void getHostDashboard_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/analytics"))
      .andExpect(status().isUnauthorized());

    then(analyticsService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getHostDashboard_ReturnsOk() throws Exception {
    AppUser host = AppUser.builder()
      .id(1L)
      .cognitoSub("987654321")
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();

    HostDashboardResponse response = new HostDashboardResponse(
      new ReviewKPIs(1L, BigDecimal.valueOf(3.0)),
      List.of(new RatingDistribution(3, 1L)),
      List.of(
        new AverageRatingByProperty(1L, "example", BigDecimal.valueOf(3.0), 1L)
      ),
      List.of(
        new AverageRatingOverTime(LocalDateTime.now(), BigDecimal.valueOf(3.0), 1L)
      ),
      new BookingKPIs(1L, BigDecimal.ONE, 1L, BigDecimal.ONE),
      List.of(new RevenueByMonth(Instant.now(), BigDecimal.ONE)),
      List.of(new BookingsByMonth(Instant.now(), 1L)),
      List.of(new RevenueByProperty(1L, "name", 1L, BigDecimal.ONE)),
      List.of(new BookingStatusDistribution("CONFIRMED", 1L))
    );

    given(appUserService.getHost(any()))
      .willReturn(host);

    given(analyticsService.getDashboard(any()))
      .willReturn(response);

    mockMvc
      .perform(get("/api/analytics").with(jwt()))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.reviewKPIs.totalReviews").value(1L))
      .andExpect(jsonPath("$.ratingDistribution.[0].rating").value(3))
      .andExpect(jsonPath("$.averageRatingByProperty.[0].name").value("example"))
      .andExpect(jsonPath("$.averageRatingOverTime.[0].averageRating").value(3.0))
      .andExpect(jsonPath("$.bookingKPIs.totalBookings").value(1))
      .andExpect(jsonPath("$.revenueByMonth.[0].revenue").value(1))
      .andExpect(jsonPath("$.bookingsByMonth.[0].bookings").value(1))
      .andExpect(jsonPath("$.revenueByProperty.[0].name").value("name"))
      .andExpect(jsonPath("$.bookingStatusDistribution.[0].bookingStatus").value("CONFIRMED"));

    then(analyticsService)
      .should()
      .getDashboard(host);
  }

}
