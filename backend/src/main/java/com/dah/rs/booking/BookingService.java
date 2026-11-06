package com.dah.rs.booking;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import com.dah.rs.property.Property;
import com.dah.rs.property.PropertyRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final PropertyRepository propertyRepository;

  public Booking createBooking(AppUser guest, BookingRequest request) {
    validateGuest(guest);
    validateTimeRange(request.checkIn(), request.checkOut());
    Property property = propertyRepository
      .findByIdForUpdate(request.propertyId())
      .orElseThrow(() -> new NotFoundException("Property not found"));
    validateNoConflict(property.getId(), request.checkIn(), request.checkOut());
    long totalNights = request.checkIn().datesUntil(request.checkOut()).count();
    BigDecimal orderTotal = property.getPrice().multiply(new BigDecimal(totalNights));
    Booking booking = Booking.builder()
      .property(property)
      .guest(guest)
      .orderTotal(orderTotal)
      .bookingStatus(BookingStatus.PENDING)
      .checkIn(request.checkIn())
      .checkOut(request.checkOut())
      .build();
    return bookingRepository.save(booking);
  }

  public Booking getBookingById(AppUser guest, Long id) {
    validateGuest(guest);
    return bookingRepository
      .findByIdAndGuestEmail(id, guest.getEmail())
      .orElseThrow(() -> new NotFoundException("Booking not found"));
  }

  public Page<BookingResponse> getBookingsByGuest(AppUser guest, int page, int size) {
    validateGuest(guest);
    return bookingRepository
      .findAllByGuestEmail(
        guest.getEmail(),
        PageRequest.of(page, size, Sort.by("createdAt").descending())
      )
      .map(BookingResponse::from);
  }

  public Page<BookingResponse> getBookingsByHost(AppUser host, int page, int size) {
    validateHost(host);
    return bookingRepository
      .findAllByPropertyHostEmail(
        host.getEmail(),
        PageRequest.of(page, size, Sort.by("createdAt").descending())
      )
      .map(BookingResponse::from);
  }

  public BookingInfo getPropertyBookingInfo(Long propertyId) {
    Property property = propertyRepository
      .findById(propertyId)
      .orElseThrow(() -> new NotFoundException("Property not found"));
    var bookings = bookingRepository.findAllByPropertyId(property.getId());
    return BookingInfo.from(property, bookings);
  }

  public Booking updateBookingStatus(AppUser guest, BookingStatusRequest request) {
    validateGuest(guest);
    Booking booking = bookingRepository
      .findByIdAndGuestEmail(request.id(), guest.getEmail())
      .orElseThrow(() -> new NotFoundException("Booking not found"));
    booking.setBookingStatus(request.bookingStatus());
    return bookingRepository.save(booking);
  }

  public void deleteBooking(AppUser guest, Long id) {
    validateGuest(guest);
    Booking booking = bookingRepository
      .findByIdAndGuestEmail(id, guest.getEmail())
      .orElseThrow(() -> new NotFoundException("Booking not found"));
    bookingRepository.delete(booking);
  }

  private void validateTimeRange(LocalDate checkIn, LocalDate checkOut) {
    if (checkIn == null || checkOut == null) {
      throw new BadRequestException("Check-in and check-out are required");
    }
    if (!checkIn.isBefore(checkOut)) {
      throw new BadRequestException("Check-in must be before check-out");
    }
    if (checkIn.isBefore(LocalDate.now())) {
      throw new BadRequestException("Check-in cannot be in the past");
    }
  }

  private void validateNoConflict(
    Long propertyId,
    LocalDate checkIn,
    LocalDate checkOut
  ) {
    if (hasConflict(propertyId, checkIn, checkOut)) {
      throw new BadRequestException("Property is already booked during this time.");
    }
  }

  private boolean hasConflict(
    Long propertyId,
    LocalDate checkIn,
    LocalDate checkOut
  ) {
    return bookingRepository.existsConflictingBooking(
      propertyId,
      checkIn,
      checkOut
    );
  }

  private void validateGuest(AppUser guest) {
    if (guest.getRole() != AppRole.GUEST) {
      throw new AccessDeniedException("User role must be GUEST");
    }
  }

  private void validateHost(AppUser host) {
    if (host.getRole() != AppRole.HOST) {
      throw new AccessDeniedException("User role must be HOST");
    }
  }

}
