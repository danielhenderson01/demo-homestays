package com.dah.rs.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dah.rs.util.TestJwt;
import com.dah.rs.util.TestUsers;
import com.dah.rs.property.Amenity;
import com.dah.rs.property.Category;
import com.dah.rs.property.Property;
import com.dah.rs.property.PropertyRepository;
import com.dah.rs.property.USState;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserRepository;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;

@Profile("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class ReviewIntegrationTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres =
    new PostgreSQLContainer(DockerImageName.parse("postgres:18"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private TestUsers testUsers;

  @Autowired
  private TestJwt testJwt;

  private AppUser guest;
  private Property property;
  private JwtRequestPostProcessor hostJwt;
  private JwtRequestPostProcessor guestJwt;

  @BeforeEach
  void setUp() {
    hostJwt = testJwt.hostJwt();
    guestJwt = testJwt.guestJwt();

    AppUser host = appUserRepository
      .findByCognitoSub(testUsers.hostSub())
      .orElseThrow();

    guest = appUserRepository
      .findByCognitoSub(testUsers.guestSub())
      .orElseThrow();

    property = createProperty(host);
  }

  @Test
  void hostCannotCreateReview() throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(property.getId(), 3, "Great place!");

    mockMvc
      .perform(
        post("/api/reviews")
          .with(hostJwt)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reviewRequest)))
      .andExpect(status().isForbidden());

    assertThat(reviewRepository.findAllByPropertyId(property.getId())).isEmpty();
  }

  @Test
  void guestCannotCreateMultipleReviewsForSameProperty() throws Exception {
    createReview(property, guest);

    ReviewRequest reviewRequest = new ReviewRequest(property.getId(), 3, "Great place!");

    mockMvc
      .perform(
        post("/api/reviews")
          .with(guestJwt)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reviewRequest)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void guestCanCreateReview() throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(property.getId(), 3, "Great place!");

    mockMvc
      .perform(
        post("/api/reviews")
          .with(guestJwt)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reviewRequest)))
      .andExpect(status().isCreated());

    assertThat(reviewRepository.findAllByPropertyId(property.getId()))
      .singleElement()
      .satisfies(review -> {
        assertThat(review.getGuest().getEmail()).isEqualTo(testUsers.guestEmail());
        assertThat(review.getProperty()).isEqualTo(property);
        assertThat(review.getRating()).isEqualTo(3);
        assertThat(review.getComment()).isEqualTo("Great place!");
      });
  }

  @Test
  void getReviewByIdReturnsReview() throws Exception {
    Review review = createReview(property, guest);

    mockMvc
      .perform(
        get("/api/reviews/{id}", review.getId())
          .with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(review.getId()))
      .andExpect(jsonPath("$.rating").value(3))
      .andExpect(jsonPath("$.comment").value("Great place!"));
  }

  @Test
  void hostCanGetReviewsForTheirProperties() throws Exception {
    Review review = createReview(property, guest);
    mockMvc
      .perform(get("/api/reviews").with(hostJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data[0].id").value(review.getId()))
      .andExpect(jsonPath("$.data[0].rating").value(3))
      .andExpect(jsonPath("$.data[0].comment").value("Great place!"));
  }

  @Test
  void guestCanGetOwnReviews() throws Exception {
    Review review = createReview(property, guest);
    mockMvc
      .perform(get("/api/reviews/guest").with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data[0].id").value(review.getId()))
      .andExpect(jsonPath("$.data[0].rating").value(3))
      .andExpect(jsonPath("$.data[0].comment").value("Great place!"));
  }

  @Test
  void getReviewsByPropertyIdReturnsReviews() throws Exception {
    Review review = createReview(property, guest);
    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}", property.getId()).with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data[0].id").value(review.getId()))
      .andExpect(jsonPath("$.data[0].rating").value(3))
      .andExpect(jsonPath("$.data[0].comment").value("Great place!"));
  }

  @Test
  void getGuestReviewForPropertyReturnsReview() throws Exception {
    Review review = createReview(property, guest);
    mockMvc
      .perform(
        get("/api/reviews/property/{propertyId}/guest", property.getId()).with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(review.getId()))
      .andExpect(jsonPath("$.rating").value(3))
      .andExpect(jsonPath("$.comment").value("Great place!"));
  }

  @Test
  void getPropertyRatingReturnsRatingSummary() throws Exception {
    createReview(property, guest);
    mockMvc
      .perform(
        get("/api/reviews/rating/{propertyId}", property.getId()).with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.numRatings").value(1))
      .andExpect(jsonPath("$.averageRating").value(3.0));
  }

  @Test
  void hostCannotDeleteReview() throws Exception {
    Review review = createReview(property, guest);

    mockMvc
      .perform(
        delete("/api/reviews/{id}", review.getId()).with(hostJwt))
      .andExpect(status().isForbidden());

    assertThat(reviewRepository.findById(review.getId())).isPresent();
  }

  @Test
  void guestCanDeleteOwnReview() throws Exception {
    Review review = createReview(property, guest);

    mockMvc
      .perform(
        delete("/api/reviews/{id}", review.getId()).with(guestJwt))
      .andExpect(status().isOk());

    assertThat(reviewRepository.findById(review.getId())).isEmpty();
  }

  private Property createProperty(AppUser host) {
    return propertyRepository.save(
      Property.builder()
        .name("Sunnyvale Apartments")
        .host(host)
        .category(Category.APARTMENT)
        .description("Lorem ipsum dolor sit amet, consectetur adipisicing elit.")
        .tagline("Where comfort meets convenience.")
        .street("123 Elm Street")
        .city("Sunnyvale")
        .state(USState.CALIFORNIA)
        .zipcode("94085")
        .beds(2)
        .baths(2)
        .squareFeet(1100)
        .price(BigDecimal.valueOf(100))
        .amenities(Set.of(Amenity.WIFI, Amenity.KITCHEN))
        .build()
    );
  }

  private Review createReview(
    Property property,
    AppUser guest
  ) {
    return reviewRepository.save(
      Review.builder()
        .property(property)
        .guest(guest)
        .rating(3)
        .comment("Great place!")
        .build()
    );
  }

}
