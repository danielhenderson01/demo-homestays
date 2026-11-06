package com.dah.rs.booking;

import static com.dah.rs.property.USState.CALIFORNIA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dah.rs.config.SecurityConfig;
import com.dah.rs.payment.PaymentService;
import com.dah.rs.property.Property;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BookingService bookingService;

  @MockitoBean
  private AppUserService appUserService;

  @MockitoBean
  private PaymentService paymentService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  private AppUser guest;
  private AppUser host;
  private Booking booking;
  private BookingRequest bookingRequest;

  @BeforeEach
  void setUp() {
    guest = createGuest();
    host = createHost();
    Property property = createProperty();
    bookingRequest = createBookingRequest();
    booking = createBooking(property);
  }

  @Test
  void createBooking_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(
        post("/api/bookings")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(bookingRequest)))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/{id}", 1L))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingsByGuest_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/guest"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingsByHost_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/host"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getPropertyBookingInfo_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/property/{propertyId}", 1L))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void updateBookingStatus_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    BookingStatusRequest request = new BookingStatusRequest(1L, BookingStatus.CONFIRMED);

    mockMvc
      .perform(
        put("/api/bookings/status")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void deleteBooking_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(delete("/api/bookings/{id}", 1L))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void deleteUnpaidGuestBookings_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(delete("/api/bookings/unpaid"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getOrderTotalSummary_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/orders/summary"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingKPIs_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/analytics/kpi"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getRevenueByMonth_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/analytics/revenue/month"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getRevenueByProperty_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/analytics/revenue/property"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingsByMonth_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/analytics/bookings/month"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void getBookingStatusDistribution_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc
      .perform(get("/api/bookings/analytics/booking-status/distribution"))
      .andExpect(status().isUnauthorized());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void createBooking_WithInvalidRequest_ReturnsBadRequest() throws Exception {
    BookingRequest invalidRequest = new BookingRequest(
      null,
      LocalDate.now().plusDays(3),
      LocalDate.now().plusDays(1)
    );

    mockMvc
      .perform(
        post("/api/bookings")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)))
      .andExpect(status().isBadRequest());

    then(bookingService).shouldHaveNoInteractions();
  }

  @Test
  void updateBookingStatus_WithInvalidRequest_ReturnsBadRequest() throws Exception {
    BookingStatusRequest invalidRequest = new BookingStatusRequest(1L, null);

    mockMvc
      .perform(
        put("/api/bookings/status")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)))
      .andExpect(status().isBadRequest());

    then(bookingService).shouldHaveNoInteractions();
  }


  @Test
  void createBooking_ReturnsCreated() throws Exception {
    given(appUserService.getGuest(any()))
      .willReturn(guest);

    given(bookingService.createBooking(any(), any()))
      .willReturn(booking);

    mockMvc
      .perform(
        post("/api/bookings")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(bookingRequest)))
      .andExpect(status().isCreated());

    then(bookingService)
      .should()
      .createBooking(guest, bookingRequest);
  }

  @Test
  void getBookingById_ReturnsOk() throws Exception {
    given(appUserService.getGuest(any()))
      .willReturn(guest);

    given(bookingService.getBookingById(guest, 1L))
      .willReturn(booking);

    mockMvc
      .perform(
        get("/api/bookings/{id}", 1L)
          .with(jwt()))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .getBookingById(guest, 1L);
  }

  @Test
  void getBookingsByGuest_ReturnsOk() throws Exception {
    given(appUserService.getGuest(any()))
      .willReturn(guest);

    given(bookingService.getBookingsByGuest(guest, 0, 10))
      .willReturn(new PageImpl<>(List.of()));

    mockMvc
      .perform(
        get("/api/bookings/guest")
          .with(jwt()))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .getBookingsByGuest(guest, 0, 10);
  }

  @Test
  void getBookingsByHost_ReturnsOk() throws Exception {
    given(appUserService.getHost(any()))
      .willReturn(host);

    given(bookingService.getBookingsByHost(host, 0, 10))
      .willReturn(new PageImpl<>(List.of()));

    mockMvc
      .perform(
        get("/api/bookings/host")
          .with(jwt()))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .getBookingsByHost(host, 0, 10);
  }

  @Test
  void getPropertyBookingInfo_ReturnsOk() throws Exception {
    given(bookingService.getPropertyBookingInfo(1L))
      .willReturn(createBookingInfo());

    mockMvc
      .perform(
        get("/api/bookings/property/{propertyId}", 1L)
          .with(jwt()))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .getPropertyBookingInfo(1L);
  }

  @Test
  void updateBookingStatus_ReturnsOk() throws Exception {
    given(appUserService.getGuest(any()))
      .willReturn(guest);

    given(bookingService.updateBookingStatus(any(), any()))
      .willReturn(booking);

    BookingStatusRequest request = new BookingStatusRequest(1L, BookingStatus.CONFIRMED);

    mockMvc
      .perform(
        put("/api/bookings/status")
          .with(jwt())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .updateBookingStatus(guest, request);
  }

  @Test
  void deleteBooking_ReturnsOk() throws Exception {
    given(
      appUserService.getGuest(any())
    ).willReturn(guest);

    mockMvc
      .perform(
        delete("/api/bookings/{id}", 1L)
          .with(jwt()))
      .andExpect(status().isOk());

    then(bookingService)
      .should()
      .deleteBooking(guest, 1L);
  }

  private Booking createBooking(Property property) {
    return Booking.builder()
      .id(1L)
      .property(property)
      .checkIn(LocalDate.now().plusDays(1))
      .checkOut(LocalDate.now().plusDays(3))
      .bookingStatus(BookingStatus.PENDING)
      .orderTotal(BigDecimal.valueOf(200))
      .guest(guest)
      .build();
  }

  private BookingRequest createBookingRequest() {
    return new BookingRequest(
      1L,
      LocalDate.now().plusDays(1),
      LocalDate.now().plusDays(3)
    );
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

  private AppUser createHost() {
    return AppUser.builder()
      .id(2L)
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();
  }

  private AppUser createGuest() {
    return AppUser.builder()
      .id(1L)
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();
  }

  private BookingInfo createBookingInfo() {
    return new BookingInfo(
      1L,
      BigDecimal.valueOf(100),
      Set.of(new BookingDates("in", "out"))
    );
  }

}
