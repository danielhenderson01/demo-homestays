package com.dah.rs.analytics;

import java.util.List;

/// @param reviewKPIs
/// @param ratingDistribution
/// @param averageRatingByProperty
/// @param averageRatingOverTime
/// @param bookingKPIs
/// @param revenueByMonth
/// @param bookingsByMonth
/// @param revenueByProperty
/// @param bookingStatusDistribution
public record HostDashboardResponse(
  ReviewKPIs reviewKPIs,
  List<RatingDistribution> ratingDistribution,
  List<AverageRatingByProperty> averageRatingByProperty,
  List<AverageRatingOverTime> averageRatingOverTime,
  BookingKPIs bookingKPIs,
  List<RevenueByMonth> revenueByMonth,
  List<BookingsByMonth> bookingsByMonth,
  List<RevenueByProperty> revenueByProperty,
  List<BookingStatusDistribution> bookingStatusDistribution
) {
}
