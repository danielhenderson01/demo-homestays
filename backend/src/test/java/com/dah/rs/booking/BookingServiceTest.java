package com.dah.rs.booking;

import static com.dah.rs.property.USState.CALIFORNIA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import com.dah.rs.property.Property;
import com.dah.rs.property.PropertyRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private BookingService bookingService;

  private AppUser guest;
  private AppUser host;
  private Property property;
  private Booking booking;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    guest = createGuest();
    host = createHost();
    property = createProperty();
    booking = createBooking(property);
  }

  @Test
  void createBooking_ShouldCreateBooking_WhenDatesAreValid() {
    BookingRequest request = new BookingRequest(
      100L,
      LocalDate.now().plusDays(1),
      LocalDate.now().plusDays(3)
    );
    when(propertyRepository.findByIdForUpdate(request.propertyId()))
      .thenReturn(Optional.of(property));
    when(bookingRepository.save(any(Booking.class)))
      .thenReturn(booking);

    Booking result = bookingService.createBooking(guest, request);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(200), result.getOrderTotal());
    verify(bookingRepository).save(any(Booking.class));
    verify(propertyRepository).findByIdForUpdate(request.propertyId());
  }

  @Test
  void createBooking_ShouldThrowException_WhenInvalidRole() {
    BookingRequest request = new BookingRequest(
      100L,
      LocalDate.now().plusDays(1),
      LocalDate.now().plusDays(3)
    );
    assertThrows(
      AccessDeniedException.class, () -> bookingService.createBooking(host, request)
    );
    verify(bookingRepository, never()).save(any(Booking.class));
    verify(propertyRepository, never()).findByIdForUpdate(request.propertyId());
  }

  @Test
  void createBooking_ShouldThrowException_WhenHasConflict() {
    var checkIn = LocalDate.now().plusDays(1);
    var checkOut = LocalDate.now().plusDays(3);

    when(propertyRepository.findByIdForUpdate(anyLong()))
      .thenReturn(Optional.of(property));
    when(bookingRepository.existsConflictingBooking(anyLong(), eq(checkIn), eq(checkOut)))
      .thenReturn(Boolean.TRUE);

    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(100L, checkIn, checkOut)
      )
    );
  }

  @Test
  void createBooking_ShouldThrowException_WhenDatesAreInvalid() {
    when(propertyRepository.findById(anyLong()))
      .thenReturn(Optional.of(property));
    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(
          100L,
          LocalDate.now().minusDays(1),
          LocalDate.now()
        )
      )
    );
    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(
          100L,
          LocalDate.now(),
          LocalDate.now().minusDays(1)
        )
      )
    );
    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(
          100L,
          LocalDate.now().plusDays(7),
          LocalDate.now().plusDays(4)
        )
      )
    );
    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(
          100L,
          null,
          LocalDate.now().plusDays(4)
        )
      )
    );
    assertThrows(
      BadRequestException.class, () -> bookingService.createBooking(
        guest,
        new BookingRequest(
          100L,
          LocalDate.now().plusDays(2),
          null
        )
      )
    );
  }

  @Test
  void getBookingById_ShouldReturnBooking_WhenExists() {
    when(bookingRepository.findByIdAndGuestEmail(1L, guest.getEmail()))
      .thenReturn(Optional.of(booking));

    Booking result = bookingService.getBookingById(guest, 1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(bookingRepository).findByIdAndGuestEmail(1L, guest.getEmail());
  }

  @Test
  void getBookingById_ShouldThrowException_WhenBookingNotFound() {
    when(bookingRepository.findByIdAndGuestEmail(1L, guest.getEmail()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () ->
        bookingService.getBookingById(guest, 1L)
    );
  }

  @Test
  void updatePaymentStatus_ShouldToggleBookingStatus() {
    booking.setBookingStatus(BookingStatus.PENDING);

    when(bookingRepository.findByIdAndGuestEmail(1L, guest.getEmail()))
      .thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(Booking.class)))
      .thenReturn(booking);

    var result = bookingService.updateBookingStatus(
      guest,
      new BookingStatusRequest(1L, BookingStatus.CONFIRMED)
    );

    assertEquals(BookingStatus.CONFIRMED, result.getBookingStatus());

    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  void getPropertyBookingInfo_ShouldReturnBookingInfo() {
    when(propertyRepository.findById(anyLong()))
      .thenReturn(Optional.of(property));
    when(bookingRepository.findAllByPropertyId(anyLong()))
      .thenReturn(List.of(booking));

    var result = bookingService.getPropertyBookingInfo(property.getId());

    assertNotNull(result);
    verify(propertyRepository).findById(anyLong());
    verify(bookingRepository).findAllByPropertyId(anyLong());
  }

  @Test
  void getBookingsByHost_ShouldThrowException_WhenInvalidRole() {
    assertThrows(
      AccessDeniedException.class, () ->
        bookingService.getBookingsByHost(guest, 0, 10)
    );
    verify(bookingRepository, never()).findAllByPropertyHostEmail(anyString(), any());
  }

  @Test
  void getBookingsByHost_ShouldReturnPage() {
    when(bookingRepository.findAllByPropertyHostEmail(anyString(), any()))
      .thenReturn(new PageImpl<>(List.of(booking)));

    var result = bookingService.getBookingsByHost(host, 0, 10);

    assertNotNull(result);
    verify(bookingRepository).findAllByPropertyHostEmail(anyString(), any());
  }

  @Test
  void getBookingsByGuest_ShouldReturnPage() {
    when(bookingRepository.findAllByGuestEmail(anyString(), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(booking)));

    var result = bookingService.getBookingsByGuest(guest, 0, 10);

    assertNotNull(result);
    verify(bookingRepository).findAllByGuestEmail(anyString(), any(Pageable.class));
  }

  @Test
  void deleteBooking_ShouldThrowException_WhenNotFound() {
    when(bookingRepository.findByIdAndGuestEmail(anyLong(), anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () ->
        bookingService.deleteBooking(guest, 99L)
    );

    verify(bookingRepository, never()).delete(any());
  }

  @Test
  void deleteBooking_ShouldDelete_WhenExists() {
    when(bookingRepository.findByIdAndGuestEmail(anyLong(), anyString()))
      .thenReturn(Optional.of(booking));

    assertDoesNotThrow(() -> bookingService.deleteBooking(guest, 1L));

    verify(bookingRepository).delete(booking);
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