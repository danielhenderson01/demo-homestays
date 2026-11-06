package com.dah.rs.property;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PropertyRepository extends JpaRepository<Property, Long> {

  boolean existsByNameAndZipcode(String name, String zipcode);

  boolean existsByNameAndZipcodeAndIdNot(String name, String zipcode, Long id);

  Optional<Property> findByIdAndHostEmail(Long id, String hostEmail);

  Page<Property> findAllByHostEmail(String hostEmail, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p from Property p WHERE p.id = :id")
  Optional<Property> findByIdForUpdate(Long id);

  @Query("""
         SELECT p FROM Property p
         WHERE
             LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND
             LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')) AND
             LOWER(p.street) LIKE LOWER(CONCAT('%', :street, '%')) AND
             LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')) AND
             LOWER(p.zipcode) LIKE LOWER(CONCAT('%', :zipcode, '%')) AND
             p.beds <= :maxBeds AND
             p.beds >= :minBeds AND
             p.baths <= :maxBaths AND
             p.baths >= :minBaths AND
             p.price <= :maxPrice AND
             p.price >= :minPrice AND
             p.squareFeet <= :maxSquareFeet AND
             p.squareFeet >= :minSquareFeet
         """)
  Page<Property> searchProperties(
    String name,
    String category,
    String street,
    String city,
    String zipcode,
    Integer minBeds,
    Integer maxBeds,
    Integer minBaths,
    Integer maxBaths,
    BigDecimal minSquareFeet,
    BigDecimal maxSquareFeet,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Pageable pageable
  );

  @Query("""
          SELECT p FROM Property p
          WHERE
              LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.street) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.zipcode) LIKE LOWER(CONCAT('%', :query, '%'))
         """)
  Page<Property> queryProperties(String query, Pageable pageable);

  @Query("""
         SELECT p FROM Property p
         WHERE
             p.host.email = :hostEmail AND
             LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND
             LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')) AND
             LOWER(p.street) LIKE LOWER(CONCAT('%', :street, '%')) AND
             LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')) AND
             LOWER(p.zipcode) LIKE LOWER(CONCAT('%', :zipcode, '%')) AND
             p.beds <= :maxBeds AND
             p.beds >= :minBeds AND
             p.baths <= :maxBaths AND
             p.baths >= :minBaths AND
             p.price <= :maxPrice AND
             p.price >= :minPrice AND
             p.squareFeet <= :maxSquareFeet AND
             p.squareFeet >= :minSquareFeet
         """)
  Page<Property> searchHostProperties(
    String hostEmail,
    String name,
    String category,
    String street,
    String city,
    String zipcode,
    Integer minBeds,
    Integer maxBeds,
    Integer minBaths,
    Integer maxBaths,
    BigDecimal minSquareFeet,
    BigDecimal maxSquareFeet,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Pageable pageable
  );

  @Query("""
          SELECT p FROM Property p
          WHERE
              p.host.email = :hostEmail AND
              LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.street) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(p.zipcode) LIKE LOWER(CONCAT('%', :query, '%'))
         """)
  Page<Property> queryHostProperties(String hostEmail, String query, Pageable pageable);

  @Query("SELECT p FROM Property p ORDER BY p.createdAt DESC LIMIT :limit")
  List<Property> findRecentProperties(int limit);

}
