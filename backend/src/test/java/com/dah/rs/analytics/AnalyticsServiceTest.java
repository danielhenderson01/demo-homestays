package com.dah.rs.analytics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dah.rs.booking.BookingRepository;
import com.dah.rs.review.ReviewRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

class AnalyticsServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private AnalyticsService analyticsService;

  private AppUser guest;
  private AppUser host;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    guest = AppUser.builder()
      .id(1L)
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();
    host = AppUser.builder()
      .id(2L)
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();
  }

  @Test
  void getDashboard_ShouldThrowException_WhenInvalidRole() {
    assertThrows(
      AccessDeniedException.class, () ->
        analyticsService.getDashboard(guest)
    );
    verify(reviewRepository, never()).getReviewKPIs(any());
    verify(reviewRepository, never()).getRatingDistribution(any(), any());
    verify(reviewRepository, never()).getAverageRatingByProperty(any(), any());
    verify(reviewRepository, never()).getAverageRatingOverTime(any(), any());
    verify(bookingRepository, never()).getBookingKPIs(any());
    verify(bookingRepository, never()).getRevenueByMonth(any(), any());
    verify(bookingRepository, never()).getBookingsByMonth(any(), any());
    verify(bookingRepository, never()).getRevenueByProperty(any(), any());
    verify(bookingRepository, never()).getBookingStatusDistribution(any(), any());
  }

  @Test
  void getDashboard_ShouldReturnHostDashboardResponse() {
    ReviewKPIs reviewKPIs = new ReviewKPIs(1L, BigDecimal.valueOf(3.0));
    when(
      reviewRepository.getReviewKPIs(anyLong())
    ).thenReturn(reviewKPIs);

    List<RatingDistribution> ratingDistribution = List.of(new RatingDistribution(3, 1L));
    when(
      reviewRepository.getRatingDistribution(any())
    ).thenReturn(ratingDistribution);

    List<AverageRatingByProperty> averageRatingByProperty = List.of(
      new AverageRatingByProperty(1L, "example", BigDecimal.valueOf(3.0), 1L)
    );
    when(
      reviewRepository.getAverageRatingByProperty(any())
    ).thenReturn(averageRatingByProperty);

    List<AverageRatingOverTime> averageRatingOverTime = List.of(
      new AverageRatingOverTime(LocalDateTime.now(), BigDecimal.valueOf(3.0), 1L)
    );
    when(
      reviewRepository.getAverageRatingOverTime(any())
    ).thenReturn(averageRatingOverTime);

    BookingKPIs bookingKPIs = new BookingKPIs(1L, BigDecimal.ONE, 1L, BigDecimal.ONE);
    when(
      bookingRepository.getBookingKPIs(anyLong())
    ).thenReturn(bookingKPIs);

    List<RevenueByMonth> revenueByMonth =
      List.of(new RevenueByMonth(Instant.now(), BigDecimal.ONE));
    when(
      bookingRepository.getRevenueByMonth(anyLong())
    ).thenReturn(revenueByMonth);

    List<BookingsByMonth> bookingsByMonth = List.of(new BookingsByMonth(Instant.now(), 1L));
    when(
      bookingRepository.getBookingsByMonth(anyLong())
    ).thenReturn(bookingsByMonth);

    List<RevenueByProperty>
      revenueByProperty = List.of(new RevenueByProperty(1L, "name", 1L, BigDecimal.ONE));
    when(
      bookingRepository.getRevenueByProperty(anyLong())
    ).thenReturn(revenueByProperty);

    List<BookingStatusDistribution>
      bookingStatusDistribution = List.of(new BookingStatusDistribution("CONFIRMED", 1L));
    when(
      bookingRepository.getBookingStatusDistribution(anyLong())
    ).thenReturn(bookingStatusDistribution);

    var result = analyticsService.getDashboard(host);

    assertNotNull(result);

    assertEquals(
      new HostDashboardResponse(
        reviewKPIs,
        ratingDistribution,
        averageRatingByProperty,
        averageRatingOverTime,
        bookingKPIs,
        revenueByMonth,
        bookingsByMonth,
        revenueByProperty,
        bookingStatusDistribution
      ),
      result
    );
  }

}