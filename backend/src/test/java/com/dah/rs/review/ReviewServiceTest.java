package com.dah.rs.review;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private ReviewService reviewService;

  private AppUser guest;
  private AppUser host;
  private Property property;
  private Review review;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    guest = AppUser.builder()
      .id(1L)
      .name("guest")
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();

    host = AppUser.builder()
      .id(2L)
      .name("host")
      .email("host@example.com")
      .role(AppRole.HOST)
      .build();

    property = Property.builder()
      .id(100L)
      .name("Test Property")
      .build();

    review = Review.builder()
      .id(1L)
      .property(property)
      .guest(guest)
      .rating(5)
      .comment("Great place!")
      .build();
  }

  @Test
  void createReview_ShouldCreateReview_WhenNoExistingReview() {
    when(propertyRepository.findById(any()))
      .thenReturn(Optional.of(property));
    when(reviewRepository.existsByPropertyIdAndGuestEmail(any(), any()))
      .thenReturn(false);
    when(reviewRepository.save(any(Review.class)))
      .thenReturn(review);

    var result = reviewService.createReview(
      guest,
      new ReviewRequest(100L, 5, "Great place!")
    );

    assertNotNull(result);
    assertEquals(5, result.rating());
    assertEquals("Great place!", result.comment());

    verify(reviewRepository).save(any(Review.class));
  }

  @Test
  void createReview_ShouldThrowException_WhenInvalidRole() {
    assertThrows(
      AccessDeniedException.class, () ->
        reviewService.createReview(
          host,
          new ReviewRequest(100L, 5, "Great place!")
        )
    );

    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void createReview_ShouldThrowException_WhenReviewAlreadyExists() {
    when(propertyRepository.findById(100L))
      .thenReturn(Optional.of(property));
    when(reviewRepository.existsByPropertyIdAndGuestEmail(100L, guest.getEmail()))
      .thenReturn(true);

    assertThrows(
      BadRequestException.class, () ->
        reviewService.createReview(
          guest,
          new ReviewRequest(100L, 5, "Great place!")
        )
    );

    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void getAllReviewsByUser_ShouldReturnPageOfReviews() {
    when(reviewRepository.findAllByGuestEmail(eq(guest.getEmail()), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(review)));

    Page<ReviewResponse> result = reviewService.getReviewsByGuest(guest, 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    verify(reviewRepository)
      .findAllByGuestEmail(eq(guest.getEmail()), any(Pageable.class));
  }

  @Test
  void getAllReviewsByProperty_Id_ShouldReturnPageOfReviews() {
    when(propertyRepository.findById(100L))
      .thenReturn(Optional.of(property));
    when(reviewRepository.findAllByPropertyId(eq(100L), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(review)));

    Page<ReviewResponse> result = reviewService.getReviewsByPropertyId(100L, 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(reviewRepository)
      .findAllByPropertyId(eq(100L), any(Pageable.class));
  }

  @Test
  void deleteReview_ShouldDeleteReview_WhenExists() {
    when(reviewRepository.findByIdAndGuestEmail(1L, guest.getEmail()))
      .thenReturn(Optional.of(review));

    assertDoesNotThrow(() -> reviewService.deleteReview(guest, 1L));

    verify(reviewRepository).delete(review);
  }

  @Test
  void deleteReviewById_ShouldThrowException_WhenReviewNotFound() {
    when(reviewRepository.findByIdAndGuestEmail(1L, guest.getEmail()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () ->
        reviewService.deleteReview(guest, 1L)
    );

    verify(reviewRepository, never()).delete(any());
  }

  @Test
  void getPropertyRating_ShouldReturnRatingResponse_WhenReviewsExist() {
    when(reviewRepository.findAllByPropertyId(100L))
      .thenReturn(List.of(
        Review.builder().rating(4).build(),
        Review.builder().rating(5).build()
      ));

    RatingResponse result = reviewService.getPropertyRating(100L);

    assertNotNull(result);
    assertEquals(4.5, result.averageRating(), 0.01);
    assertEquals(2, result.numRatings());

    verify(reviewRepository).findAllByPropertyId(100L);
  }

  @Test
  void getPropertyRating_ShouldReturnZero_WhenNoReviewsExist() {
    when(reviewRepository.findAllByPropertyId(100L)).thenReturn(List.of());

    RatingResponse result = reviewService.getPropertyRating(100L);

    assertNotNull(result);
    assertEquals(0.0, result.averageRating());
    assertEquals(0, result.numRatings());

    verify(reviewRepository).findAllByPropertyId(100L);
  }

  @Test
  void getReviewByGuestAndPropertyId_ShouldReturnReview_WhenExists() {
    when(reviewRepository.findByPropertyIdAndGuestEmail(anyLong(), anyString()))
      .thenReturn(Optional.of(review));

    var result = reviewService.getReviewByGuestAndPropertyId(guest, 100L);

    assertNotNull(result);
    verify(reviewRepository).findByPropertyIdAndGuestEmail(anyLong(), anyString());
  }

  @Test
  void getReviewByIdAndGuestEmail_ShouldReturnReview_WhenExists() {
    when(reviewRepository.findByIdAndGuestEmail(anyLong(), anyString()))
      .thenReturn(Optional.of(review));

    var result = reviewService.getReview(guest, 1L);

    assertNotNull(result);
    verify(reviewRepository).findByIdAndGuestEmail(1L, guest.getEmail());
  }

  @Test
  void getReviewsByHostEmail_ShouldThrowException_WhenInvalidRole() {
    assertThrows(
      AccessDeniedException.class, () ->
        reviewService.getReviewsByHostEmail(guest, 0, 10)
    );
    verify(reviewRepository, never()).findAllByPropertyHostEmail(any(), any());
  }

  @Test
  void getReviewsByHostEmail_ShouldReturnPage() {
    when(reviewRepository.findAllByPropertyHostEmail(anyString(), any()))
      .thenReturn(new PageImpl<>(List.of(review)));

    var result = reviewService.getReviewsByHostEmail(host, 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    verify(reviewRepository).findAllByPropertyHostEmail(anyString(), any());
  }

}