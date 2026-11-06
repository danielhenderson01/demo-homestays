package com.dah.rs.review;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import com.dah.rs.property.Property;
import com.dah.rs.property.PropertyRepository;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
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
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final PropertyRepository propertyRepository;

  public ReviewResponse createReview(AppUser guest, ReviewRequest request) {
    validateGuest(guest);

    Property property = propertyRepository
      .findById(request.propertyId())
      .orElseThrow(() -> new NotFoundException("Property not found"));

    if (
      reviewRepository.existsByPropertyIdAndGuestEmail(request.propertyId(), guest.getEmail())
    ) {
      throw new BadRequestException("User has already created a review for this property");
    }

    return ReviewResponse.from(
      reviewRepository.save(
        Review.builder()
          .guest(guest)
          .property(property)
          .rating(request.rating())
          .comment(request.comment())
          .build()
      ));
  }

  public ReviewResponse getReview(AppUser guest, Long reviewId) {
    validateGuest(guest);
    return reviewRepository
      .findByIdAndGuestEmail(reviewId, guest.getEmail())
      .map(ReviewResponse::from)
      .orElseThrow(() -> new NotFoundException("Review not found."));
  }

  public Page<ReviewResponse> getReviewsByHostEmail(AppUser host, int page, int size) {
    validateHost(host);
    return reviewRepository.findAllByPropertyHostEmail(
      host.getEmail(),
      PageRequest.of(page, size, Sort.by("createdAt").descending())
    ).map(ReviewResponse::from);
  }

  public Page<ReviewResponse> getReviewsByGuest(AppUser guest, int page, int size) {
    validateGuest(guest);
    return reviewRepository.findAllByGuestEmail(
      guest.getEmail(),
      PageRequest.of(page, size, Sort.by("createdAt").descending())
    ).map(ReviewResponse::from);
  }

  public Page<ReviewResponse> getReviewsByPropertyId(Long propertyId, int page, int size) {
    Property property = propertyRepository
      .findById(propertyId)
      .orElseThrow(() -> new NotFoundException("Property not found"));
    return reviewRepository.findAllByPropertyId(
      property.getId(),
      PageRequest.of(page, size, Sort.by("createdAt").descending())
    ).map(ReviewResponse::from);
  }

  public ReviewResponse getReviewByGuestAndPropertyId(AppUser guest, Long propertyId) {
    validateGuest(guest);
    return reviewRepository
      .findByPropertyIdAndGuestEmail(propertyId, guest.getEmail())
      .map(ReviewResponse::from)
      .orElse(null);
  }

  public RatingResponse getPropertyRating(Long propertyId) {
    return RatingResponse.fromReviews(
      reviewRepository.findAllByPropertyId(propertyId)
    );
  }

  public void deleteReview(AppUser guest, Long reviewId) {
    validateGuest(guest);
    reviewRepository
      .findByIdAndGuestEmail(reviewId, guest.getEmail())
      .ifPresentOrElse(
        reviewRepository::delete,
        () -> {
          throw new NotFoundException("Review not found");
        }
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
