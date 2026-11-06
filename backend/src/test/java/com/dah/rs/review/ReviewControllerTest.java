package com.dah.rs.review;

import static com.dah.rs.property.USState.CALIFORNIA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dah.rs.config.SecurityConfig;
import com.dah.rs.property.Property;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
public class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReviewService reviewService;

  @MockitoBean
  private AppUserService appUserService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  private AppUser host;
  private AppUser guest;
  private Review review;
  private ReviewRequest reviewRequest;

  @BeforeEach
  void setUp() {
    guest = AppUser.builder()
      .id(1L)
      .cognitoSub("123456789")
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();

    host = AppUser.builder()
      .id(2L)
      .cognitoSub("987654321")
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();

    Property property = Property.builder()
      .id(1L)
      .name("Sample Property")
      .host(host)
      .description("Modern apartments with ample natural light and open layouts.")
      .tagline("Where comfort meets convenience.")
      .street("123 Elm Street")
      .city("Sunnyvale")
      .state(CALIFORNIA)
      .zipcode("94085")
      .beds(2)
      .baths(2)
      .squareFeet(1100)
      .price(BigDecimal.valueOf(100))
      .build();

    review = Review.builder()
      .id(1L)
      .property(property)
      .guest(guest)
      .rating(5)
      .comment("Great place!")
      .build();

    reviewRequest = new ReviewRequest(1L, 5, "Great place!");
  }

  /* --- Unauthorized --- */

  @Test
  void createReview_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        post("/api/reviews")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reviewRequest))
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews/{id}", 1L)
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewsByHostEmail_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews")
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewsByGuest_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews/guest")
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewByPropertyId_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}", 1L)
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewByPropertyIdAndGuest_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}/guest", 1L)
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getPropertyRating_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        get("/api/reviews/rating/{propertyId}", 1L)
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void deleteReview_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        delete("/api/reviews/{id}", 1L)
      )
      .andExpect(
        status()
          .isUnauthorized()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  @Test
  void getReviewKPIs_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/reviews/analytics/kpi"))
      .andExpect(status().isUnauthorized());
    then(reviewService).shouldHaveNoInteractions();
  }

  @Test
  void getRatingDistribution_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/reviews/analytics/rating/distribution"))
      .andExpect(status().isUnauthorized());
    then(reviewService).shouldHaveNoInteractions();
  }

  @Test
  void getAverageRatingByProperty_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/reviews/analytics/average/rating/property"))
      .andExpect(status().isUnauthorized());
    then(reviewService).shouldHaveNoInteractions();
  }

  @Test
  void getAverageRatingOverTime_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/reviews/analytics/average/rating/time"))
      .andExpect(status().isUnauthorized());
    then(reviewService).shouldHaveNoInteractions();
  }

  /* --- Bad Request --- */

  @Test
  void createReview_WithInvalidRequest_ReturnsBadRequest() throws Exception {
    ReviewRequest invalidRequest = new ReviewRequest(null, 0, "");
    mockMvc
      .perform(
        post("/api/reviews")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest))
      )
      .andExpect(
        status()
          .isBadRequest()
      );
    then(reviewService)
      .shouldHaveNoInteractions();
  }

  /* --- Ok or Created --- */

  @Test
  void createReview_ReturnsCreated() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    given(
      reviewService.createReview(guest, reviewRequest)
    ).willReturn(ReviewResponse.from(review));

    mockMvc
      .perform(
        post("/api/reviews")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reviewRequest))
      )
      .andExpect(
        status()
          .isCreated()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.id")
          .value(1L)
      )
      .andExpect(
        jsonPath("$.rating")
          .value(5)
      )
      .andExpect(
        jsonPath("$.comment")
          .value("Great place!")
      );

    then(reviewService)
      .should()
      .createReview(guest, reviewRequest);
  }

  @Test
  void getReviewById_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    given(
      reviewService.getReview(guest, 1L)
    ).willReturn(ReviewResponse.from(review));

    mockMvc
      .perform(
        get("/api/reviews/{id}", 1L)
          .with(jwt())
      )
      .andExpect(
        status().isOk()
      )
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.id").value(1L)
      )
      .andExpect(
        jsonPath("$.rating").value(5)
      )
      .andExpect(
        jsonPath("$.comment").value("Great place!")
      );

    then(reviewService)
      .should()
      .getReview(guest, 1L);
  }

  @Test
  void getReviewsByHostEmail_ReturnsOk() throws Exception {
    given(
      appUserService.getHost(any())
    ).willReturn(host);

    given(
      reviewService.getReviewsByHostEmail(host, 0, 10)
    ).willReturn(
      new PageImpl<>(List.of(ReviewResponse.from(review)))
    );

    mockMvc
      .perform(
        get("/api/reviews")
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.data[0].id")
          .value(1L)
      )
      .andExpect(
        jsonPath("$.data[0].rating")
          .value(5)
      )
      .andExpect(
        jsonPath("$.data[0].comment")
          .value("Great place!")
      );

    then(reviewService)
      .should()
      .getReviewsByHostEmail(host, 0, 10);
  }

  @Test
  void getReviewsByGuest_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    given(
      reviewService.getReviewsByGuest(guest, 0, 10)
    ).willReturn(
      new PageImpl<>(List.of(ReviewResponse.from(review)))
    );

    mockMvc
      .perform(
        get("/api/reviews/guest")
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.data[0].id")
          .value(1L)
      )
      .andExpect(
        jsonPath("$.data[0].rating")
          .value(5)
      )
      .andExpect(
        jsonPath("$.data[0].comment")
          .value("Great place!")
      );

    then(reviewService)
      .should()
      .getReviewsByGuest(guest, 0, 10);
  }

  @Test
  void getReviewsByPropertyId_ReturnsOk() throws Exception {
    given(
      reviewService.getReviewsByPropertyId(1L, 0, 10)
    ).willReturn(
      new PageImpl<>(List.of(ReviewResponse.from(review)))
    );

    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}", 1L)
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.data[0].id")
          .value(1L)
      )
      .andExpect(
        jsonPath("$.data[0].rating")
          .value(5)
      )
      .andExpect(
        jsonPath("$.data[0].comment")
          .value("Great place!")
      );

    then(reviewService)
      .should()
      .getReviewsByPropertyId(1L, 0, 10);
  }

  @Test
  void getReviewByPropertyIdAndGuest_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    given(
      reviewService.getReviewByGuestAndPropertyId(guest, 1L)
    ).willReturn(ReviewResponse.from(review));

    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}/guest", 1L)
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.id")
          .value(1L)
      )
      .andExpect(
        jsonPath("$.rating")
          .value(5)
      )
      .andExpect(
        jsonPath("$.comment")
          .value("Great place!")
      );

    then(reviewService)
      .should()
      .getReviewByGuestAndPropertyId(guest, 1L);
  }

  @Test
  void getReviewByPropertyIdAndGuest_WhenNoReviewFound_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    given(
      reviewService.getReviewByGuestAndPropertyId(guest, 1L)
    ).willReturn(null);

    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}/guest", 1L)
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      );

    then(reviewService)
      .should()
      .getReviewByGuestAndPropertyId(guest, 1L);
  }

  @Test
  void getPropertyRating_ReturnsOk() throws Exception {
    RatingResponse ratingResponse = new RatingResponse(5.0, 1);

    given(
      reviewService.getPropertyRating(1L)
    ).willReturn(ratingResponse);

    mockMvc
      .perform(
        get("/api/reviews/rating/{propertyId}", 1L)
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      )
      .andExpect(
        content()
          .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(
        jsonPath("$.averageRating")
          .value(5.0)
      )
      .andExpect(
        jsonPath("$.numRatings")
          .value(1)
      );

    then(reviewService)
      .should()
      .getPropertyRating(1L);
  }

  @Test
  void deleteReview_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    mockMvc
      .perform(
        delete("/api/reviews/{id}", 1L)
          .with(jwt())
      )
      .andExpect(
        status()
          .isOk()
      );

    then(reviewService)
      .should()
      .deleteReview(guest, 1L);
  }

}
