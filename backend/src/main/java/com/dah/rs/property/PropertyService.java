package com.dah.rs.property;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import com.dah.rs.image.ImageService;
import com.dah.rs.image.PropertyImage;
import com.dah.rs.image.PropertyImageRepository;
import com.dah.rs.image.PropertyImageResponse;
import com.dah.rs.image.ImageRequest;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.math.BigDecimal;
import java.util.List;
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
public class PropertyService {

  private final PropertyRepository propertyRepository;
  private final PropertyImageRepository imageRepository;
  private final ImageService imageService;

  public PropertyResponse createProperty(AppUser host, CreatePropertyRequest request) {
    validateHost(host);

    if (propertyRepository.existsByNameAndZipcode(request.name(), request.zipcode())) {
      throw new BadRequestException(
        "A property with the name '(%s)' already exists in '(%s)'"
          .formatted(request.name(), request.zipcode())
      );
    }

    Property property = propertyRepository.save(
      Property.builder()
        .host(host)
        .category(request.category())
        .name(request.name())
        .tagline(request.tagline())
        .description(request.description())
        .street(request.street())
        .city(request.city())
        .state(request.state())
        .zipcode(request.zipcode())
        .beds(request.beds())
        .baths(request.baths())
        .squareFeet(request.squareFeet())
        .price(request.price())
        .amenities(request.amenities())
        .build()
    );

    String cloudFrontUrl = imageService.getCloudFrontUrl();
    String s3Prefix = imageService.getS3SamplePrefix();

    imageRepository.saveAll(
      List.of(
        PropertyImage
          .builder()
          .property(property)
          .imageUrl(cloudFrontUrl + "/" + s3Prefix + "/d.jpg")
          .objectKey(s3Prefix + "/d.jpg")
          .build(),
        PropertyImage
          .builder()
          .property(property)
          .imageUrl(cloudFrontUrl + "/" + s3Prefix + "/h.jpg")
          .objectKey(s3Prefix + "/h.jpg")
          .build(),
        PropertyImage
          .builder()
          .property(property)
          .imageUrl(cloudFrontUrl + "/" + s3Prefix + "/j.jpg")
          .objectKey(s3Prefix + "/j.jpg")
          .build()
      ));

    return PropertyResponse.from(property);
  }

  public List<PropertyResponse> getRecentProperties(int limit) {
    return propertyRepository
      .findRecentProperties(limit)
      .stream()
      .map(PropertyResponse::from)
      .toList();
  }

  public Long countProperties() {
    return propertyRepository.count();
  }

  public Page<PropertyResponse> getPropertiesByHost(AppUser host, int page, int size) {
    validateHost(host);
    return propertyRepository.findAllByHostEmail(
      host.getEmail(),
      PageRequest.of(page, size, Sort.by("createdAt").descending())
    ).map(PropertyResponse::from);
  }

  public PropertyResponse getHostPropertyById(AppUser host, Long propertyId) {
    validateHost(host);
    return propertyRepository
      .findByIdAndHostEmail(propertyId, host.getEmail())
      .map(PropertyResponse::from)
      .orElseThrow(() -> new NotFoundException("Property not found"));
  }

  public PropertyResponse getPropertyById(Long propertyId) {
    return propertyRepository
      .findById(propertyId)
      .map(PropertyResponse::from)
      .orElseThrow(() -> new NotFoundException("Property not found"));
  }

  public void deleteProperty(AppUser host, Long propertyId) {
    validateHost(host);
    Property property = propertyRepository
      .findByIdAndHostEmail(propertyId, host.getEmail())
      .orElseThrow(() -> new NotFoundException("Property not found"));
    propertyRepository.delete(property);
  }

  public PropertyResponse updateProperty(
    AppUser host,
    Long propertyId,
    UpdatePropertyRequest request
  ) {
    validateHost(host);

    Property p = propertyRepository
      .findByIdAndHostEmail(propertyId, host.getEmail())
      .orElseThrow(() -> new NotFoundException("Property not found"));

    if (
      propertyRepository.existsByNameAndZipcodeAndIdNot(
        request.name() == null ? p.getName() : request.name(),
        request.zipcode() == null ? p.getZipcode() : request.zipcode(),
        propertyId
      )
    ) {
      throw new BadRequestException(
        "A property with the name '(%s)' already exists in '(%s)'"
          .formatted(request.name(), request.zipcode())
      );
    }

    if (request.name() != null) p.setName(request.name());
    if (request.category() != null) p.setCategory(request.category());
    if (request.description() != null) p.setDescription(request.description());
    if (request.street() != null) p.setStreet(request.street());
    if (request.city() != null) p.setCity(request.city());
    if (request.state() != null) p.setState(request.state());
    if (request.zipcode() != null) p.setZipcode(request.zipcode());
    if (request.beds() != null) p.setBeds(request.beds());
    if (request.baths() != null) p.setBaths(request.baths());
    if (request.squareFeet() != null) p.setSquareFeet(request.squareFeet());
    if (request.price() != null) p.setPrice(request.price());
    if (request.amenities() != null) p.setAmenities(request.amenities());

    return PropertyResponse.from(propertyRepository.save(p));
  }

  public Page<PropertyImageResponse> getPropertyImages(
    AppUser host,
    Long propertyId,
    int page,
    int size
  ) {
    validateHost(host);
    return imageRepository
      .findAllByPropertyIdAndPropertyHostEmail(
        propertyId,
        host.getEmail(),
        PageRequest.of(page, size, Sort.by("createdAt").descending())
      ).map(PropertyImageResponse::from);
  }

  public void deleteImage(AppUser host, Long imageId) {
    validateHost(host);

    PropertyImage image = imageRepository
      .findByIdAndPropertyHostEmail(imageId, host.getEmail())
      .orElseThrow(() -> new NotFoundException("Image not found"));

    imageRepository.delete(image);
  }

  public void addImagesToProperty(
    AppUser host,
    Long propertyId,
    List<ImageRequest> request
  ) {
    validateHost(host);

    Property property = propertyRepository
      .findByIdAndHostEmail(propertyId, host.getEmail())
      .orElseThrow(() -> new NotFoundException("Property not found"));

    String cloudFrontUrl = imageService.getCloudFrontUrl();

    List<PropertyImage> images = request
      .stream()
      .map(r -> PropertyImage
        .builder()
        .property(property)
        .imageUrl("%s/%s".formatted(cloudFrontUrl, r.objectKey()))
        .objectKey(r.objectKey())
        .build())
      .toList();

    imageRepository.saveAll(images);
  }

  public Page<PropertyResponse> searchProperties(
    String query,
    String name,
    String category,
    String street,
    String city,
    String zipcode,
    int minBeds,
    int maxBeds,
    int minBaths,
    int maxBaths,
    BigDecimal minSquareFeet,
    BigDecimal maxSquareFeet,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    int page,
    int size
  ) {
    PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    if (!query.isBlank()) {
      return propertyRepository.queryProperties(query, pageable).map(PropertyResponse::from);
    }
    return propertyRepository.searchProperties(
      name,
      category,
      street,
      city,
      zipcode,
      minBeds,
      maxBeds,
      minBaths,
      maxBaths,
      minSquareFeet,
      maxSquareFeet,
      minPrice,
      maxPrice,
      pageable
    ).map(PropertyResponse::from);
  }

  public Page<PropertyResponse> searchHostProperties(
    AppUser host,
    String query,
    String name,
    String category,
    String street,
    String city,
    String zipcode,
    int minBeds,
    int maxBeds,
    int minBaths,
    int maxBaths,
    BigDecimal minSquareFeet,
    BigDecimal maxSquareFeet,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    int page,
    int size
  ) {
    validateHost(host);

    PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    if (!query.isBlank()) {
      return propertyRepository
        .queryHostProperties(host.getEmail(), query, pageable)
        .map(PropertyResponse::from);
    }

    return propertyRepository.searchHostProperties(
      host.getEmail(),
      name,
      category,
      street,
      city,
      zipcode,
      minBeds,
      maxBeds,
      minBaths,
      maxBaths,
      minSquareFeet,
      maxSquareFeet,
      minPrice,
      maxPrice,
      pageable
    ).map(PropertyResponse::from);
  }

  private void validateHost(AppUser host) {
    if (host.getRole() != AppRole.HOST) {
      throw new AccessDeniedException("User role must be HOST");
    }
  }

}
