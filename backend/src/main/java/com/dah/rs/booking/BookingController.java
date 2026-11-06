package com.dah.rs.booking;

import com.dah.rs.common.PageResponse;
import com.dah.rs.payment.PaymentResponse;
import com.dah.rs.payment.PaymentService;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingService bookingService;
  private final AppUserService appUserService;
  private final PaymentService paymentService;

  @PostMapping()
  public ResponseEntity<?> createBooking(
    @AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody BookingRequest request
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    Booking booking = bookingService.createBooking(guest, request);
    PaymentResponse paymentResponse = paymentService.createPaymentLink(booking);
    return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getBookingById(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    Booking booking = bookingService.getBookingById(guest, id);
    return ResponseEntity.ok(BookingResponse.from(booking));
  }

  @GetMapping("/guest")
  public ResponseEntity<? extends PageResponse<?>> getBookingsByGuest(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    Page<BookingResponse> bookings =
      bookingService.getBookingsByGuest(guest, page, size);
    return ResponseEntity.ok(PageResponse.from(bookings));
  }

  @GetMapping("/host")
  public ResponseEntity<?> getBookingsByHost(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    AppUser host = appUserService.getHost(jwt);
    Page<BookingResponse> bookings =
      bookingService.getBookingsByHost(host, page, size);
    PageResponse<?> pageResponse = PageResponse.from(bookings);
    return ResponseEntity.ok(pageResponse);
  }

  @GetMapping("/property/{propertyId}")
  public ResponseEntity<?> getPropertyBookingInfo(@PathVariable Long propertyId) {
    BookingInfo bookingInfo = bookingService.getPropertyBookingInfo(propertyId);
    return ResponseEntity.ok(bookingInfo);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateBookingStatus(
    @AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody BookingStatusRequest request
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    Booking booking = bookingService.updateBookingStatus(guest, request);
    return ResponseEntity.ok(BookingResponse.from(booking));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBooking(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    bookingService.deleteBooking(guest, id);
    return ResponseEntity.ok().build();
  }

}
