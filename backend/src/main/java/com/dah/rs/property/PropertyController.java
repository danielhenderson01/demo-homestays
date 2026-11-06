package com.dah.rs.property;

import com.dah.rs.common.PageResponse;
import com.dah.rs.image.ImageRequest;
import com.dah.rs.image.ImageService;
import com.dah.rs.image.PresignResponse;
import com.dah.rs.image.PropertyImageResponse;
import com.dah.rs.user.AppUser;
import com.dah.rs.user.AppUserService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
public class PropertyController {

  private final PropertyService propertyService;
  private final AppUserService appUserService;
  private final ImageService imageService;

  @PostMapping()
  public ResponseEntity<?> createProperty(
    @AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody CreatePropertyRequest request
  ) {
    PropertyResponse property = propertyService.createProperty(
      appUserService.getHost(jwt),
      request
    );
    return new ResponseEntity<>(property, HttpStatus.CREATED);
  }

  @GetMapping("/recent")
  public ResponseEntity<?> getRecentProperties(
    @RequestParam(name = "limit", defaultValue = "6") int limit
  ) {
    return ResponseEntity.ok(propertyService.getRecentProperties(limit));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPropertyById(@PathVariable Long id) {
    return ResponseEntity.ok(propertyService.getPropertyById(id));
  }

  @GetMapping("/host/{id}")
  public ResponseEntity<?> getHostPropertyById(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    AppUser host = appUserService.getHost(jwt);
    PropertyResponse property = propertyService.getHostPropertyById(host, id);
    return ResponseEntity.ok(property);
  }

  @GetMapping("/count")
  public ResponseEntity<?> countProperties() {
    Long count = propertyService.countProperties();
    return ResponseEntity.ok(Map.of("count", count));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateProperty(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id,
    @Valid @RequestBody UpdatePropertyRequest request
  ) {
    PropertyResponse
      property = propertyService.updateProperty(appUserService.getHost(jwt), id, request);
    return ResponseEntity.ok(property);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProperty(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    propertyService.deleteProperty(appUserService.getHost(jwt), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/images")
  public ResponseEntity<?> getPropertyImages(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    AppUser host = appUserService.getHost(jwt);
    Page<PropertyImageResponse> images =
      propertyService.getPropertyImages(host, id, page, size);
    return ResponseEntity.ok(PageResponse.from(images));
  }

  @PostMapping("/{id}/images/upload-url")
  public ResponseEntity<?> generateUploadUrl(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id,
    @Valid @RequestBody ImageRequest request
  ) {
    AppUser host = appUserService.getHost(jwt);
    propertyService.getHostPropertyById(host, id);
    String uploadUrl = imageService.generateUploadUrl(host, request.objectKey());
    return ResponseEntity.ok(new PresignResponse(uploadUrl));
  }

  @PutMapping("/{id}/images")
  public ResponseEntity<?> addImagesToProperty(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id,
    @RequestBody List<ImageRequest> request
  ) {
    AppUser host = appUserService.getHost(jwt);
    propertyService.addImagesToProperty(host, id, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/images")
  public ResponseEntity<?> deletePropertyImage(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id
  ) {
    propertyService.deleteImage(appUserService.getHost(jwt), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping()
  public ResponseEntity<?> searchProperties(
    @RequestParam(name = "query", defaultValue = "") String query,
    @RequestParam(name = "name", defaultValue = "") String name,
    @RequestParam(name = "category", defaultValue = "") String category,
    @RequestParam(name = "street", defaultValue = "") String street,
    @RequestParam(name = "city", defaultValue = "") String city,
    @RequestParam(name = "zipcode", defaultValue = "") String zipcode,
    @RequestParam(name = "minBeds", defaultValue = "0") int minBeds,
    @RequestParam(name = "maxBeds", defaultValue = "99") int maxBeds,
    @RequestParam(name = "minBaths", defaultValue = "0") int minBaths,
    @RequestParam(name = "maxBaths", defaultValue = "99") int maxBaths,
    @RequestParam(name = "minSquareFeet", defaultValue = "0") String minSquareFeet,
    @RequestParam(name = "maxSquareFeet", defaultValue = "99999") String maxSquareFeet,
    @RequestParam(name = "minPrice", defaultValue = "0") String minPrice,
    @RequestParam(name = "maxPrice", defaultValue = "99999") String maxPrice,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    Page<PropertyResponse> properties = propertyService.searchProperties(
      query,
      name,
      category,
      street,
      city,
      zipcode,
      minBeds,
      maxBeds,
      minBaths,
      maxBaths,
      new BigDecimal(minSquareFeet),
      new BigDecimal(maxSquareFeet),
      new BigDecimal(minPrice),
      new BigDecimal(maxPrice),
      page,
      size
    );
    return ResponseEntity.ok(PageResponse.from(properties));
  }

  @GetMapping("/host")
  public ResponseEntity<?> getPropertiesByHost(
    @AuthenticationPrincipal Jwt jwt,
    @RequestParam(name = "query", defaultValue = "") String query,
    @RequestParam(name = "name", defaultValue = "") String name,
    @RequestParam(name = "category", defaultValue = "") String category,
    @RequestParam(name = "street", defaultValue = "") String street,
    @RequestParam(name = "city", defaultValue = "") String city,
    @RequestParam(name = "zipcode", defaultValue = "") String zipcode,
    @RequestParam(name = "minBeds", defaultValue = "0") int minBeds,
    @RequestParam(name = "maxBeds", defaultValue = "99") int maxBeds,
    @RequestParam(name = "minBaths", defaultValue = "0") int minBaths,
    @RequestParam(name = "maxBaths", defaultValue = "99") int maxBaths,
    @RequestParam(name = "minSquareFeet", defaultValue = "0") String minSquareFeet,
    @RequestParam(name = "maxSquareFeet", defaultValue = "99999") String maxSquareFeet,
    @RequestParam(name = "minPrice", defaultValue = "0") String minPrice,
    @RequestParam(name = "maxPrice", defaultValue = "99999") String maxPrice,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    Page<PropertyResponse> properties = propertyService.searchHostProperties(
      appUserService.getHost(jwt),
      query,
      name,
      category,
      street,
      city,
      zipcode,
      minBeds,
      maxBeds,
      minBaths,
      maxBaths,
      new BigDecimal(minSquareFeet),
      new BigDecimal(maxSquareFeet),
      new BigDecimal(minPrice),
      new BigDecimal(maxPrice),
      page,
      size
    );
    return ResponseEntity.ok(PageResponse.from(properties));
  }

}
