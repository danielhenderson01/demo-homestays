package com.dah.rs.booking;

import com.dah.rs.property.Property;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @param propertyId    Long
 * @param propertyPrice BigDecimal
 * @param bookingDates  Set
 */
public record BookingInfo(
  Long propertyId,
  BigDecimal propertyPrice,
  Set<BookingDates> bookingDates
) {

  public static BookingInfo from(Property property, List<Booking> bookings) {
    return new BookingInfo(
      property.getId(),
      property.getPrice(),
      bookings
        .stream()
        .map(BookingDates::from)
        .collect(Collectors.toSet())
    );
  }

}
