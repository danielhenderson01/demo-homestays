package com.dah.rs.review;

import com.dah.rs.analytics.AverageRatingByProperty;
import com.dah.rs.analytics.AverageRatingOverTime;
import com.dah.rs.analytics.RatingDistribution;
import com.dah.rs.analytics.ReviewKPIs;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Page<Review> findAllByGuestEmail(String guestEmail, Pageable pageable);

  Page<Review> findAllByPropertyId(Long propertyId, Pageable pageable);

  Page<Review> findAllByPropertyHostEmail(String email, Pageable pageable);

  Optional<Review> findByPropertyIdAndGuestEmail(Long propertyId, String guestEmail);

  Optional<Review> findByIdAndGuestEmail(Long id, String guestEmail);

  List<Review> findAllByPropertyId(Long propertyId);

  boolean existsByPropertyIdAndGuestEmail(Long propertyId, String guestEmail);

  @Query(
    value = """
            SELECT COUNT(r.id) AS total_reviews, ROUND(AVG(r.rating), 2) AS average_rating
              FROM review r
                       JOIN property p
                       ON p.id = r.property_id
             WHERE p.host_id = :hostId;
            """,
    nativeQuery = true
  )
  ReviewKPIs getReviewKPIs(Long hostId);

  @Query(
    value = """
            SELECT r.rating, COUNT(*) AS count
              FROM review r
                       JOIN property p
                       ON p.id = r.property_id
             WHERE p.host_id = :hostId
             GROUP BY r.rating
             ORDER BY r.rating;
            """,
    nativeQuery = true
  )
  Page<RatingDistribution> getRatingDistribution(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT p.id, p.name, ROUND(AVG(r.rating), 2) AS average_rating, COUNT(r.id) AS review_count
              FROM property p
                       LEFT JOIN review r
                       ON r.property_id = p.id
             WHERE p.host_id = :hostId
             GROUP BY p.id, p.name
             ORDER BY average_rating DESC NULLS LAST;
            """,
    nativeQuery = true
  )
  Page<AverageRatingByProperty> getAverageRatingByProperty(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT DATE_TRUNC('month', r.created_at) AS month, ROUND(AVG(r.rating), 2) AS average_rating,
                   COUNT(*) AS review_count
              FROM review r
                       JOIN property p
                       ON p.id = r.property_id
             WHERE p.host_id = :hostId
             GROUP BY DATE_TRUNC('month', r.created_at)
             ORDER BY month;
            """,
    nativeQuery = true
  )
  Page<AverageRatingOverTime> getAverageRatingOverTime(Long hostId, Pageable pageable);

  @Query(
    value = """
            SELECT r.rating, COUNT(*) AS count
              FROM review r
                       JOIN property p
                       ON p.id = r.property_id
             WHERE p.host_id = :hostId
             GROUP BY r.rating
             ORDER BY r.rating;
            """,
    nativeQuery = true
  )
  List<RatingDistribution> getRatingDistribution(Long hostId);

  @Query(
    value = """
            SELECT p.id, p.name, ROUND(AVG(r.rating), 2) AS average_rating, COUNT(r.id) AS review_count
              FROM property p
                       LEFT JOIN review r
                       ON r.property_id = p.id
             WHERE p.host_id = :hostId
             GROUP BY p.id, p.name
             ORDER BY average_rating DESC NULLS LAST;
            """,
    nativeQuery = true
  )
  List<AverageRatingByProperty> getAverageRatingByProperty(Long hostId);

  @Query(
    value = """
            SELECT DATE_TRUNC('month', r.created_at) AS month, ROUND(AVG(r.rating), 2) AS average_rating,
                   COUNT(*) AS review_count
              FROM review r
                       JOIN property p
                       ON p.id = r.property_id
             WHERE p.host_id = :hostId
             GROUP BY DATE_TRUNC('month', r.created_at)
             ORDER BY month;
            """,
    nativeQuery = true
  )
  List<AverageRatingOverTime> getAverageRatingOverTime(Long hostId);

}
