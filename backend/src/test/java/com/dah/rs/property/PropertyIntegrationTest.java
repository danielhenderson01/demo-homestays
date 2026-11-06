package com.dah.rs.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dah.rs.util.TestJwt;
import com.dah.rs.util.TestUsers;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserRepository;
import java.math.BigDecimal;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
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
class PropertyIntegrationTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres =
    new PostgreSQLContainer(DockerImageName.parse("postgres:18"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AppUserRepository appUserRepository;

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private TestUsers testUsers;

  @Autowired
  private TestJwt testJwt;

  private AppUser host;
  private JwtRequestPostProcessor hostJwt;
  private JwtRequestPostProcessor guestJwt;

  @BeforeEach
  void setUp() {
    hostJwt = testJwt.hostJwt();
    guestJwt = testJwt.guestJwt();
    host = appUserRepository
      .findByCognitoSub(testUsers.hostSub())
      .orElseThrow();
  }

  @Test
  void hostCanCreateProperty() throws Exception {
    CreatePropertyRequest createRequest = createPropertyRequest();

    mockMvc.perform(
        post("/api/properties")
          .with(hostJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createRequest)))
      .andExpect(status().isCreated());

    assertThat(propertyRepository.findAll())
      .singleElement()
      .satisfies(p -> {
        assertThat(p.getName()).isEqualTo(createRequest.name());
        assertThat(p.getBeds()).isEqualTo(createRequest.beds());
        assertThat(p.getPrice()).isEqualTo(createRequest.price());
        assertThat(p.getHost().getEmail()).isEqualTo(testUsers.hostEmail());
      });
  }

  @Test
  void guestCannotCreateProperty() throws Exception {
    CreatePropertyRequest createRequest = createPropertyRequest();

    mockMvc.perform(
        post("/api/properties")
          .with(guestJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createRequest)))
      .andExpect(status().isForbidden());

    assertThat(propertyRepository.findAll()).isEmpty();
  }

  @Test
  void hostCanUpdateProperty() throws Exception {
    Property property = createProperty(host);

    UpdatePropertyRequest updateRequest = updatePropertyRequest(property.getId());

    mockMvc.perform(
        put("/api/properties/{id}", property.getId())
          .with(hostJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Property"))
      .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(125)));

    assertThat(
      propertyRepository.findById(property.getId()).get().getName()
    ).isEqualTo("Updated Property");

    assertThat(
      propertyRepository.findById(property.getId()).get().getPrice()
    ).isEqualTo(BigDecimal.valueOf(125));
  }

  @Test
  void getPropertyByIdReturnsProperty() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties/{id}", property.getId()).with(jwt()))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.price").value(property.getPrice()))
      .andExpect(jsonPath("$.name").value(property.getName()));
  }

  @Test
  void hostCanGetOwnProperty() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties/host/{id}", property.getId()).with(hostJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.price").value(property.getPrice()))
      .andExpect(jsonPath("$.name").value(property.getName()));
  }

  @Test
  void guestCannotGetHostProperty() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties/host/{id}", property.getId()).with(guestJwt))
      .andExpect(status().isForbidden());
  }

  @Test
  void getRecentPropertiesReturnsProperties() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties/recent").with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$[0].price").value(property.getPrice()))
      .andExpect(jsonPath("$[0].name").value(property.getName()));
  }

  @Test
  void countPropertiesReturnsNumberOfProperties() throws Exception {
    createProperty(host);
    mockMvc
      .perform(get("/api/properties/count").with(jwt()))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.count").value(1));
  }

  @Test
  void searchPropertiesReturnsProperties() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties").with(jwt()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data[0].price").value(property.getPrice()))
      .andExpect(jsonPath("$.data[0].name").value(property.getName()));
  }

  @Test
  void guestCannotSearchHostProperties() throws Exception {
    mockMvc
      .perform(get("/api/properties/host").with(guestJwt))
      .andExpect(status().isForbidden());
  }

  @Test
  void hostCanSearchOwnProperties() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(get("/api/properties/host").with(hostJwt))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data[0].price").value(property.getPrice()))
      .andExpect(jsonPath("$.data[0].name").value(property.getName()));
  }

  @Test
  void guestCannotDeleteProperty() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(delete("/api/properties/{id}", property.getId()).with(guestJwt))
      .andExpect(status().isForbidden());
    assertThat(propertyRepository.findAll()).isNotEmpty();
  }

  @Test
  void hostCanDeleteProperty() throws Exception {
    Property property = createProperty(host);
    mockMvc
      .perform(delete("/api/properties/{id}", property.getId()).with(hostJwt))
      .andExpect(status().isOk());
    assertThat(propertyRepository.findAll()).isEmpty();
  }

  private static @NonNull CreatePropertyRequest createPropertyRequest() {
    return new CreatePropertyRequest(
      "Sample Property",
      Category.APARTMENT,
      "Beautiful apartment",
      "Tagline here",
      "Street",
      "City",
      USState.COLORADO,
      "12345",
      2,
      2,
      1500,
      BigDecimal.valueOf(100),
      Set.of(Amenity.LAUNDRY, Amenity.DISHWASHER)
    );
  }

  private static @NonNull UpdatePropertyRequest updatePropertyRequest(Long propertyId) {
    return new UpdatePropertyRequest(
      propertyId,
      "Updated Property",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      BigDecimal.valueOf(125),
      null
    );
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

}
