package com.dah.rs.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
class BookingIntegrationTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres =
    new PostgreSQLContainer(DockerImageName.parse("postgres:18"));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookingRepository bookingRepository;

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
  void invalidRequestDoesNotCreateBooking() throws Exception {
    var requests = List.of(
      new BookingRequest( // invalid check in
        property.getId(),
        LocalDate.now().minusDays(2),
        LocalDate.now().plusDays(2)
      ),
      new BookingRequest( // invalid check out
        property.getId(),
        LocalDate.now().plusDays(2),
        LocalDate.now().minusDays(2)
      ),
      new BookingRequest( // null check in
        property.getId(),
        null,
        LocalDate.now().plusDays(2)
      ),
      new BookingRequest( // null check out
        property.getId(),
        LocalDate.now().plusDays(2),
        null
      )
    );

    for (var req : requests) {
      mockMvc
        .perform(
          post("/api/bookings")
            .with(guestJwt)
            .contentType(APPLICATION_JSON)
            .content(
              objectMapper.writeValueAsString(req)
            ))
        .andExpect(status().isBadRequest());
      // Verify that no booking was created
      assertThat(bookingRepository.findAllByPropertyId(property.getId())).isEmpty();
    }
  }

  @Test
  void hostCannotCreateBooking() throws Exception {
    BookingRequest createRequest = new BookingRequest(
      property.getId(),
      LocalDate.now().plusDays(1),
      LocalDate.now().plusDays(3)
    );

    mockMvc
      .perform(
        post("/api/bookings")
          .with(hostJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createRequest)))
      .andExpect(status().isForbidden());

    assertThat(bookingRepository.findAllByPropertyId(property.getId())).isEmpty();
  }

  @Test
  void guestCanCreateBooking() throws Exception {
    BookingRequest createRequest = new BookingRequest(
      property.getId(),
      LocalDate.now().plusDays(1),
      LocalDate.now().plusDays(3)
    );

    mockMvc
      .perform(
        post("/api/bookings")
          .with(guestJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(createRequest)))
      .andExpect(status().isCreated())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.paymentUrl").isString());

    assertThat(bookingRepository.findAllByPropertyId(property.getId()))
      .singleElement()
      .satisfies(b -> {
        assertThat(b.getBookingStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(b.getCheckIn()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(b.getCheckOut()).isEqualTo(LocalDate.now().plusDays(3));
      });
  }

  @Test
  void guestCanGetBookingById() throws Exception {
    Booking booking = createBooking(property, guest);
    mockMvc
      .perform(get("/api/bookings/{id}", booking.getId()).with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(booking.getId()))
      .andExpect(jsonPath("$.propertyId").value(property.getId()))
      .andExpect(jsonPath("$.checkIn").value(LocalDate.now().plusDays(1).toString()))
      .andExpect(jsonPath("$.checkOut").value(LocalDate.now().plusDays(3).toString()))
      .andExpect(jsonPath("$.bookingStatus").value("PENDING"));
  }

  @Test
  void hostCannotGetBookingById() throws Exception {
    Booking booking = createBooking(property, guest);
    mockMvc
      .perform(get("/api/bookings/{id}", booking.getId()).with(hostJwt))
      .andExpect(status().isForbidden());
  }

  @Test
  void guestCanGetOwnBookings() throws Exception {
    Booking booking = createBooking(property, guest);
    mockMvc
      .perform(get("/api/bookings/guest").with(guestJwt))
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data[0].id").value(booking.getId()))
      .andExpect(jsonPath("$.data[0].propertyId").value(property.getId()))
      .andExpect(jsonPath("$.data[0].checkIn").value(LocalDate.now().plusDays(1).toString()))
      .andExpect(jsonPath("$.data[0].checkOut").value(LocalDate.now().plusDays(3).toString()))
      .andExpect(jsonPath("$.data[0].bookingStatus").value("PENDING"));
  }

  @Test
  void hostCannotGetGuestBookings() throws Exception {
    mockMvc
      .perform(get("/api/bookings/guest").with(hostJwt))
      .andExpect(status().isForbidden());
  }

  @Test
  void hostCanGetBookingsForTheirProperties() throws Exception {
    Booking booking = createBooking(property, guest);
    mockMvc
      .perform(get("/api/bookings/host").with(hostJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.data[0].id").value(booking.getId()))
      .andExpect(jsonPath("$.data[0].propertyId").value(property.getId()))
      .andExpect(jsonPath("$.data[0].checkIn").value(LocalDate.now().plusDays(1).toString()))
      .andExpect(jsonPath("$.data[0].checkOut").value(LocalDate.now().plusDays(3).toString()))
      .andExpect(jsonPath("$.data[0].bookingStatus").value("PENDING"));
  }

  @Test
  void guestCannotGetHostBookings() throws Exception {
    mockMvc
      .perform(get("/api/bookings/host").with(guestJwt))
      .andExpect(status().isForbidden());
  }

  @Test
  void getPropertyBookingInfoReturnsBookingInfoForProperty() throws Exception {
    Booking booking = createBooking(property, guest);
    mockMvc
      .perform(get("/api/bookings/property/{propertyId}", property.getId()).with(guestJwt))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
      .andExpect(jsonPath("$.bookingDates[0].checkIn").value(
        booking.getCheckIn().toString() + "T00:00:00"))
      .andExpect(jsonPath("$.bookingDates[0].checkOut").value(
        booking.getCheckOut().toString() + "T00:00:00"));
  }

  @Test
  void invalidRequestDoesNotUpdateBookingStatus() throws Exception {
    Booking booking = createBooking(property, guest);

    mockMvc
      .perform(
        put("/api/bookings/status")
          .with(guestJwt)
          .contentType(APPLICATION_JSON)
          .content("""
                   {
                     "id": 1,
                     "bookingStatus": "FAILED"
                   }
                   """)
      )
      .andExpect(status().isBadRequest());

    assertThat(
      bookingRepository.findById(booking.getId()).get().getBookingStatus()
    ).isEqualTo(
      BookingStatus.PENDING
    );
  }

  @Test
  void hostCannotUpdateBookingStatus() throws Exception {
    Booking booking = createBooking(property, guest);
    BookingStatusRequest request = new BookingStatusRequest(
      booking.getId(),
      BookingStatus.CONFIRMED
    );

    mockMvc
      .perform(
        put("/api/bookings/status")
          .with(hostJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isForbidden());

    assertThat(
      bookingRepository.findById(booking.getId()).get().getBookingStatus()
    ).isEqualTo(
      BookingStatus.PENDING
    );
  }

  @Test
  void guestCanUpdateBookingStatus() throws Exception {
    Booking booking = createBooking(property, guest);
    BookingStatusRequest request = new BookingStatusRequest(
      booking.getId(),
      BookingStatus.CONFIRMED
    );

    mockMvc
      .perform(
        put("/api/bookings/status")
          .with(guestJwt)
          .contentType(APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk());

    assertThat(
      bookingRepository.findById(booking.getId()).get().getBookingStatus()
    ).isEqualTo(
      BookingStatus.CONFIRMED
    );
  }

  @Test
  void hostCannotDeleteBooking() throws Exception {
    Booking booking = createBooking(property, guest);

    mockMvc
      .perform(delete("/api/bookings/{id}", booking.getId()).with(hostJwt))
      .andExpect(status().isForbidden());

    assertThat(bookingRepository.findById(booking.getId())).isPresent();
  }

  @Test
  void guestCanDeleteOwnBooking() throws Exception {
    Booking booking = createBooking(property, guest);

    mockMvc
      .perform(delete("/api/bookings/{id}", booking.getId()).with(guestJwt))
      .andExpect(status().isOk());

    assertThat(bookingRepository.findById(booking.getId())).isEmpty();
  }

  private Booking createBooking(Property property, AppUser guest) {
    LocalDate checkIn = LocalDate.now().plusDays(1);
    LocalDate checkOut = LocalDate.now().plusDays(3);
    return bookingRepository.save(
      Booking.builder()
        .property(property)
        .guest(guest)
        .checkIn(checkIn)
        .checkOut(checkOut)
        .orderTotal(
          property
            .getPrice()
            .multiply(new BigDecimal(checkIn.datesUntil(checkOut).count()))
        )
        .build()
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
