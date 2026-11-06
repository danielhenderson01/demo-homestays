package com.dah.rs.booking;

import com.dah.rs.image.PropertyImage;
import com.dah.rs.property.Address;
import com.dah.rs.property.Property;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.GuestResponse;
import com.dah.rs.user.HostResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @param id             Long
 * @param propertyId     Long
 * @param guest          GuestResponse
 * @param host           HostResponse
 * @param propertyImages List (String)
 * @param propertyName   String
 * @param address        Address
 * @param totalNights    long
 * @param pricePerNight  BigDecimal
 * @param orderTotal     BigDecimal
 * @param checkIn        LocalDate
 * @param checkOut       LocalDate
 * @param bookingStatus  BookingStatus
 */
public record BookingResponse(
  Long id,
  Long propertyId,
  GuestResponse guest,
  HostResponse host,
  List<String> propertyImages,
  String propertyName,
  Address address,
  long totalNights,
  BigDecimal pricePerNight,
  BigDecimal orderTotal,
  LocalDate checkIn,
  LocalDate checkOut,
  BookingStatus bookingStatus
) {

  public static BookingResponse from(Booking booking) {
    Property property = booking.getProperty();
    AppUser host = booking.getProperty().getHost();
    AppUser guest = booking.getGuest();
    return new BookingResponse(
      booking.getId(),
      property.getId(),
      GuestResponse.from(guest),
      HostResponse.from(host),
      property.getImages().stream().map(PropertyImage::getImageUrl).toList(),
      property.getName(),
      new Address(
        property.getStreet(),
        property.getCity(),
        property.getState().getValue(),
        property.getZipcode()
      ),
      booking.getCheckIn().datesUntil(booking.getCheckOut()).count(),
      property.getPrice(),
      booking.getOrderTotal(),
      booking.getCheckIn(),
      booking.getCheckOut(),
      booking.getBookingStatus()
    );
  }

}
