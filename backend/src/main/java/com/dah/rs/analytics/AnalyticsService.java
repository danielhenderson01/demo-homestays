package com.dah.rs.analytics;

import com.dah.rs.booking.BookingRepository;
import com.dah.rs.review.ReviewRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final ReviewRepository reviewRepository;
  private final BookingRepository bookingRepository;

  /// - KPI cards: total revenue, bookings, booked nights, average rating
  /// - Revenue by month: line chart
  /// - Bookings by month: bar chart
  /// - Revenue by property: horizontal bar chart
  /// - Rating distribution: 1–5 star bar chart
  /// - Average rating by property: bar chart
  /// - Average rating over time: line chart
  /// - Booking status distribution: donut chart
  /// - Date range selector (Last 30 days / Last 6 months / Last year / All time)
  /// - Optional property filter
  ///
  /// @param host AppUser
  /// @return HostDashboardResponse
  public HostDashboardResponse getDashboard(AppUser host) {
    validateHost(host);
    return new HostDashboardResponse(
      reviewRepository.getReviewKPIs(host.getId()),
      reviewRepository.getRatingDistribution(host.getId()),
      reviewRepository.getAverageRatingByProperty(host.getId()),
      reviewRepository.getAverageRatingOverTime(host.getId()),
      bookingRepository.getBookingKPIs(host.getId()),
      bookingRepository.getRevenueByMonth(host.getId()),
      bookingRepository.getBookingsByMonth(host.getId()),
      bookingRepository.getRevenueByProperty(host.getId()),
      bookingRepository.getBookingStatusDistribution(host.getId())
    );
  }

  private void validateHost(AppUser host) {
    if (host.getRole() != AppRole.HOST) {
      throw new AccessDeniedException("User role must be HOST");
    }
  }

}
