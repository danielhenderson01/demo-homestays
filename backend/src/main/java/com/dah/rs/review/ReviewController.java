package com.dah.rs.review;

import com.dah.rs.common.PageResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final AppUserService appUserService;

  @PostMapping()
  public ResponseEntity<?> createReview(
    @AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody ReviewRequest request
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    ReviewResponse review = reviewService.createReview(guest, request);
    return new ResponseEntity<>(review, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getReviewById(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    ReviewResponse review = reviewService.getReview(guest, id);
    return ResponseEntity.ok(review);
  }

  @GetMapping()
  public ResponseEntity<?> getReviewsByHostEmail(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    AppUser host = appUserService.getHost(jwt);
    Page<ReviewResponse> reviews = reviewService.getReviewsByHostEmail(host, page, size);
    return ResponseEntity.ok(PageResponse.from(reviews));
  }

  @GetMapping("/guest")
  public ResponseEntity<?> getReviewsByGuest(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    Page<ReviewResponse> reviews = reviewService.getReviewsByGuest(guest, page, size);
    return ResponseEntity.ok(PageResponse.from(reviews));
  }

  @GetMapping("/property/{propertyId}")
  public ResponseEntity<?> getReviewsByPropertyId(
    @PathVariable Long propertyId,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    Page<ReviewResponse> reviews = reviewService.getReviewsByPropertyId(propertyId, page, size);
    return ResponseEntity.ok(PageResponse.from(reviews));
  }

  @GetMapping("/property/{propertyId}/guest")
  public ResponseEntity<?> getReviewByPropertyIdAndGuest(
    @PathVariable Long propertyId,
    @AuthenticationPrincipal Jwt jwt
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    ReviewResponse review = reviewService.getReviewByGuestAndPropertyId(guest, propertyId);
    return review == null
      ? ResponseEntity.ok().build()
      : ResponseEntity.ok(review);
  }

  @GetMapping("/rating/{propertyId}")
  public ResponseEntity<?> getPropertyRating(@PathVariable Long propertyId) {
    RatingResponse rating = reviewService.getPropertyRating(propertyId);
    return ResponseEntity.ok(rating);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteReview(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    AppUser guest = appUserService.getGuest(jwt);
    reviewService.deleteReview(guest, id);
    return ResponseEntity.ok().build();
  }

}
