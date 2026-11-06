package com.dah.rs.booking;

import com.dah.rs.analytics.BookingKPIs;
import com.dah.rs.analytics.BookingStatusDistribution;
import com.dah.rs.analytics.BookingsByMonth;
import com.dah.rs.analytics.RevenueByMonth;
import com.dah.rs.analytics.RevenueByProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  Optional<Booking> findByIdAndGuestEmail(Long id, String guestEmail);

  Page<Booking> findAllByGuestEmail(String guestEmail, Pageable pageable);

  Page<Booking> findAllByPropertyHostEmail(String hostEmail, Pageable pageable);

  List<Booking> findByGuestEmailAndBookingStatus(String guestEmail, BookingStatus bookingStatus);

  List<Booking> findAllByPropertyId(Long id);

  @Query("""
         select count(b) > 0
         from Booking b
         where b.property.id = :propertyId
         and b.checkIn < :requestedCheckOut
         and b.checkOut > :requestedCheckIn
         """)
  boolean existsConflictingBooking(
    Long propertyId,
    LocalDate requestedCheckIn,
    LocalDate requestedCheckOut
  );

  @Query("""
         select b.orderTotal
         from Booking b
         where b.property.host.id = :id
         and b.bookingStatus = :bookingStatus
         order by b.orderTotal
         """)
  List<BigDecimal> findOrderTotalsByHostId(
    Long id,
    BookingStatus bookingStatus
  );

  @Query(
    value = """
            SELECT COUNT(DISTINCT b.id) AS total_bookings, COALESCE(SUM(b.order_total), 0) AS total_revenue,
                   COALESCE(SUM(b.check_out - b.check_in), 0) AS booked_nights,
                   ROUND(AVG(b.order_total), 2) AS average_booking_value
              FROM booking b
                       JOIN property p
                       ON p.id = b.property_id
             WHERE p.host_id = :hostId
               AND b.booking_status = 'CONFIRMED';
            """,
    nativeQuery = true
  )
  BookingKPIs getBookingKPIs(Long hostId);

  @Query(
    value = """
             SELECT DATE_TRUNC('month', b.check_in) AS month, SUM(b.order_total) AS revenue
             FROM booking b
                      JOIN property p
                      ON p.id = b.property_id
            WHERE p.host_id = :hostId
              AND b.booking_status = 'CONFIRMED'
            GROUP BY DATE_TRUNC('month', b.check_in)
            ORDER BY month;
            """,
    nativeQuery = true
  )
  Page<RevenueByMonth> getRevenueByMonth(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT DATE_TRUNC('month', b.check_in) AS month, COUNT(*) AS bookings
              FROM booking b
                       JOIN property p
                       ON p.id = b.property_id
             WHERE p.host_id = :hostId
               AND b.booking_status = 'CONFIRMED'
             GROUP BY DATE_TRUNC('month', b.check_in)
             ORDER BY month;
            """,
    nativeQuery = true
  )
  Page<BookingsByMonth> getBookingsByMonth(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT p.id, p.name, COUNT(b.id) AS booking_count, SUM(b.order_total) AS revenue
              FROM property p
                       LEFT JOIN booking b
                       ON b.property_id = p.id AND b.booking_status = 'CONFIRMED'
             WHERE p.host_id = :hostId
             GROUP BY p.id, p.name
             ORDER BY revenue DESC NULLS LAST;
            
            """,
    nativeQuery = true
  )
  Page<RevenueByProperty> getRevenueByProperty(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT b.booking_status, COUNT(*) AS count
              FROM booking b
                       JOIN property p
                       ON p.id = b.property_id
             WHERE p.host_id = :hostId
             GROUP BY b.booking_status
             ORDER BY count DESC;
            """,
    nativeQuery = true
  )
  Page<BookingStatusDistribution> getBookingStatusDistribution(Long hostId, Pageable pageable);

  @Query(
    value = """
             SELECT DATE_TRUNC('month', b.check_in) AS month, SUM(b.order_total) AS revenue
             FROM booking b
                      JOIN property p
                      ON p.id = b.property_id
            WHERE p.host_id = :hostId
              AND b.booking_status = 'CONFIRMED'
            GROUP BY DATE_TRUNC('month', b.check_in)
            ORDER BY month;
            """,
    nativeQuery = true
  )
  List<RevenueByMonth> getRevenueByMonth(Long hostId);

  @Query(
    value = """
            SELECT DATE_TRUNC('month', b.check_in) AS month, COUNT(*) AS bookings
              FROM booking b
                       JOIN property p
                       ON p.id = b.property_id
             WHERE p.host_id = :hostId
               AND b.booking_status = 'CONFIRMED'
             GROUP BY DATE_TRUNC('month', b.check_in)
             ORDER BY month;
            """,
    nativeQuery = true
  )
  List<BookingsByMonth> getBookingsByMonth(Long hostId);

  @Query(
    value = """
            SELECT p.id, p.name, COUNT(b.id) AS booking_count, SUM(b.order_total) AS revenue
              FROM property p
                       LEFT JOIN booking b
                       ON b.property_id = p.id AND b.booking_status = 'CONFIRMED'
             WHERE p.host_id = :hostId
             GROUP BY p.id, p.name
             ORDER BY revenue DESC NULLS LAST;
            
            """,
    nativeQuery = true
  )
  List<RevenueByProperty> getRevenueByProperty(Long hostId);

  @Query(
    value = """
            SELECT b.booking_status, COUNT(*) AS count
              FROM booking b
                       JOIN property p
                       ON p.id = b.property_id
             WHERE p.host_id = :hostId
             GROUP BY b.booking_status
             ORDER BY count DESC;
            """,
    nativeQuery = true
  )
  List<BookingStatusDistribution> getBookingStatusDistribution(Long hostId);

}
