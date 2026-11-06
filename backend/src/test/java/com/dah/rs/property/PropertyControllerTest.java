package com.dah.rs.property;

import static com.dah.rs.property.USState.CALIFORNIA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dah.rs.config.SecurityConfig;
import com.dah.rs.image.ImageService;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;
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

@WebMvcTest(PropertyController.class)
@Import(SecurityConfig.class)
public class PropertyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PropertyService propertyService;

  @MockitoBean
  private AppUserService appUserService;

  @MockitoBean
  private ImageService imageService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  private AppUser host;
  private Property property;

  @BeforeEach
  void setUp() {
    host = createHost();
    property = createProperty();
  }

  @Test
  void createProperty_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    var createPropertyRequest = createCreatePropertyRequest();

    mockMvc
      .perform(
        post("/api/properties")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createPropertyRequest)))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void createProperty_WithInvalidRequest_ReturnsBadRequest() throws Exception {
    var invalidRequest = createInvalidCreatePropertyRequest();

    mockMvc
      .perform(
        post("/api/properties")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)))
      .andExpect(status().isBadRequest());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void createProperty_ReturnsCreated() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    var createPropertyRequest = createCreatePropertyRequest();

    given(appUserService.getHost(any()))
      .willReturn(host);

    given(propertyService.createProperty(any(), any()))
      .willReturn(propertyResponse);

    mockMvc
      .perform(
        post("/api/properties")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createPropertyRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Sample Property"));

    then(propertyService)
      .should()
      .createProperty(host, createPropertyRequest);
  }

  @Test
  void updateProperty_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    var request = createUpdatePropertyRequest();

    mockMvc
      .perform(
        put("/api/properties/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void updateProperty_WithInvalidRequest_ReturnsBadRequest() throws Exception {
    var request = createInvalidUpdatePropertyRequest();

    mockMvc
      .perform(
        put("/api/properties/{id}", 1L)
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());

    then(propertyService)
      .shouldHaveNoInteractions();
  }


  @Test
  void updateProperty_UpdatesProperty() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    var request = createUpdatePropertyRequest();

    given(appUserService.getHost(any()))
      .willReturn(host);

    property.setName("Updated Property");
    property.setPrice(BigDecimal.valueOf(125));

    given(propertyService.updateProperty(any(), any(), any()))
      .willReturn(propertyResponse);

    mockMvc
      .perform(
        put("/api/properties/{id}", 1L)
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L));

    then(propertyService)
      .should()
      .updateProperty(host, 1L, request);
  }

  @Test
  void deleteProperty_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        delete("/api/properties/{id}", 1L))
      .andExpect(status().isUnauthorized());

    then(propertyService)
      .shouldHaveNoInteractions();
  }

  @Test
  void deleteProperty_ReturnsOk() throws Exception {
    given(appUserService.getHost(any()))
      .willReturn(host);

    mockMvc
      .perform(
        delete("/api/properties/{id}", 1L)
          .with(jwt()))
      .andExpect(status().isOk());

    then(propertyService)
      .should()
      .deleteProperty(host, 1L);
  }


  @Test
  void getPropertyById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/properties/{id}", 1L))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void getPropertyById_ReturnsProperty() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    given(propertyService.getPropertyById(1L))
      .willReturn(propertyResponse);

    mockMvc
      .perform(
        get("/api/properties/{id}", 1L)
          .with(jwt()))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Sample Property"));

    then(propertyService)
      .should()
      .getPropertyById(1L);
  }

  @Test
  void getHostPropertyById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/properties/host/{id}", 1L))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void getHostPropertyById_ReturnsProperty() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    given(appUserService.getHost(any()))
      .willReturn(host);

    given(propertyService.getHostPropertyById(host, 1L))
      .willReturn(propertyResponse);

    mockMvc
      .perform(
        get("/api/properties/host/{id}", 1L)
          .with(jwt()))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Sample Property"));

    then(propertyService)
      .should()
      .getHostPropertyById(host, 1L);
  }

  @Test
  void searchProperties_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/properties"))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void searchProperties_ReturnsPropertiesPage() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    var propertyPage = new PageImpl<>(List.of(propertyResponse));

    given(
      propertyService.searchProperties(
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        99,
        0,
        99,
        new BigDecimal(0),
        new BigDecimal(99999),
        new BigDecimal(0),
        new BigDecimal(99999),
        0,
        10
      )
    ).willReturn(propertyPage);

    mockMvc
      .perform(
        get("/api/properties")
          .with(jwt()))
      .andExpect(status().isOk());

    then(propertyService)
      .should()
      .searchProperties(
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        99,
        0,
        99,
        new BigDecimal(0),
        new BigDecimal(99999),
        new BigDecimal(0),
        new BigDecimal(99999),
        0,
        10
      );
  }

  @Test
  void getPropertiesByHost_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/properties/host"))
      .andExpect(status().isUnauthorized());

    then(propertyService).shouldHaveNoInteractions();
  }

  @Test
  void getPropertiesByHost_ReturnsPropertiesPage() throws Exception {
    var propertyResponse = PropertyResponse.from(property);

    var propertyPage = new PageImpl<>(List.of(propertyResponse));

    given(appUserService.getHost(any()))
      .willReturn(host);

    given(
      propertyService.searchHostProperties(
        host,
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        99,
        0,
        99,
        new BigDecimal(0),
        new BigDecimal(99999),
        new BigDecimal(0),
        new BigDecimal(99999),
        0,
        10
      )
    ).willReturn(propertyPage);

    mockMvc
      .perform(
        get("/api/properties/host")
          .with(jwt()))
      .andExpect(status().isOk());

    then(propertyService)
      .should()
      .searchHostProperties(
        host,
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        99,
        0,
        99,
        new BigDecimal(0),
        new BigDecimal(99999),
        new BigDecimal(0),
        new BigDecimal(99999),
        0,
        10
      );
  }

  private static AppUser createHost() {
    return AppUser.builder()
      .id(1L)
      .cognitoSub("123456789")
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();
  }

  private Property createProperty() {
    return Property.builder()
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
  }

  private static @NonNull CreatePropertyRequest createCreatePropertyRequest() {
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

  private static @NonNull CreatePropertyRequest createInvalidCreatePropertyRequest() {
    return new CreatePropertyRequest(
      "",
      Category.APARTMENT,
      "",
      "",
      "",
      "",
      USState.COLORADO,
      "",
      2,
      2,
      1500,
      BigDecimal.valueOf(100),
      Set.of(Amenity.LAUNDRY, Amenity.DISHWASHER)
    );
  }

  private static @NonNull UpdatePropertyRequest createUpdatePropertyRequest() {
    return new UpdatePropertyRequest(
      1L,
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

  private static @NonNull UpdatePropertyRequest createInvalidUpdatePropertyRequest() {
    return new UpdatePropertyRequest(
      1L,
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
      null,
      BigDecimal.valueOf(-99),
      null
    );
  }

}
