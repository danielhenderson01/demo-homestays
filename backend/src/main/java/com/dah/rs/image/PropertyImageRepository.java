package com.dah.rs.image;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

  Page<PropertyImage> findAllByPropertyIdAndPropertyHostEmail(
    Long propertyId,
    String email,
    Pageable pageable
  );

  Optional<PropertyImage> findByIdAndPropertyHostEmail(Long id, String email);

}
