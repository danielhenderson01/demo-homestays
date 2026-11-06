package com.dah.rs.property;

import static com.dah.rs.property.Amenity.DISHWASHER;
import static com.dah.rs.property.Amenity.KITCHEN;
import static com.dah.rs.property.Amenity.LAUNDRY;
import static com.dah.rs.property.Amenity.SMART_TV;
import static com.dah.rs.property.Amenity.WIFI;
import static com.dah.rs.property.Category.APARTMENT;
import static com.dah.rs.property.USState.CALIFORNIA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.exception.NotFoundException;
import com.dah.rs.image.ImageService;
import com.dah.rs.image.PropertyImage;
import com.dah.rs.image.PropertyImageRepository;
import com.dah.rs.image.ImageRequest;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

class PropertyServiceTest {

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private PropertyImageRepository imageRepository;

  @Mock
  private ImageService imageService;

  @InjectMocks
  private PropertyService propertyService;

  private AppUser host;
  private AppUser guest;
  private CreatePropertyRequest createPropertyRequest;
  private UpdatePropertyRequest updatePropertyRequest;
  private Property property;
  private List<PropertyImage> propertyImages;
  private PropertyImage propertyImage;

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

    createPropertyRequest = new CreatePropertyRequest(
      "Sample Property",
      Category.APARTMENT,
      "Beautiful apartment",
      "Tagline here",
      "Street",
      "City",
      USState.COLORADO,
      "12345",
      2,
      2,
      1500,
      BigDecimal.valueOf(100),
      Set.of(Amenity.LAUNDRY, Amenity.DISHWASHER)
    );

    updatePropertyRequest = new UpdatePropertyRequest(
      1L,
      "Updated Property",
      Category.APARTMENT,
      "Beautiful apartment",
      "Tagline here",
      "Street",
      "City",
      USState.COLORADO,
      "12345",
      2,
      2,
      1500,
      BigDecimal.valueOf(150),
      Set.of(Amenity.LAUNDRY, Amenity.DISHWASHER)
    );

    property = Property.builder()
      .id(1L)
      .name("Sunnyvale Apartments")
      .host(host)
      .category(APARTMENT)
      .description("Modern apartments with ample natural light and open layouts.")
      .tagline("Where comfort meets convenience.")
      .street("123 Elm Street")
      .city("Sunnyvale")
      .state(CALIFORNIA)
      .zipcode("94085")
      .beds(2)
      .baths(2)
      .squareFeet(1100)
      .price(BigDecimal.valueOf(100))
      .amenities(
        Set.of(
          WIFI,
          KITCHEN,
          LAUNDRY,
          DISHWASHER,
          SMART_TV
        )
      )
      .images(List.of())
      .build();

    propertyImage = PropertyImage.builder()
      .id(1L)
      .property(property)
      .imageUrl("image.jpg")
      .objectKey("object-key")
      .build();

    propertyImages = List.of(propertyImage);
  }

  @Test
  void createProperty_ShouldThrowException_WhenPropertyIsDuplicate() {
    when(propertyRepository.existsByNameAndZipcode(anyString(), anyString()))
      .thenReturn(true);

    assertThrows(
      BadRequestException.class,
      () -> propertyService.createProperty(host, createPropertyRequest)
    );

    verify(propertyRepository).existsByNameAndZipcode(anyString(), anyString());
    verify(propertyRepository, never()).save(any(Property.class));
  }

  @Test
  void createProperty_ShouldThrowException_WhenInvalidRole() {
    AppUser guest = AppUser.builder()
      .id(2L)
      .email("guest@example.com")
      .role(AppRole.GUEST)
      .build();

    assertThrows(
      AccessDeniedException.class, () ->
        propertyService.createProperty(guest, createPropertyRequest)
    );

    verify(propertyRepository, never()).save(any(Property.class));
  }

  @Test
  void createProperty_ShouldSaveProperty_WhenNotDuplicate() {
    when(propertyRepository.existsByNameAndZipcode(anyString(), anyString()))
      .thenReturn(false);
    when(propertyRepository.save(any(Property.class)))
      .thenReturn(property);
    when(imageRepository.saveAll(anyList()))
      .thenReturn(propertyImages);

    assertDoesNotThrow(
      () -> propertyService.createProperty(host, createPropertyRequest)
    );

    verify(propertyRepository).existsByNameAndZipcode(anyString(), anyString());
    verify(propertyRepository).save(any(Property.class));
    verify(imageRepository).saveAll(anyList());
  }

  @Test
  void getPropertiesByHost_ShouldReturnPageOfProperties() {
    Page<Property> propertyPage = new PageImpl<>(List.of(property));
    when(propertyRepository.findAllByHostEmail(anyString(), any(Pageable.class)))
      .thenReturn(propertyPage);

    var result = propertyService.getPropertiesByHost(host, 0, 10);

    assertNotNull(result);
    verify(propertyRepository).findAllByHostEmail(anyString(), any(Pageable.class));
  }

  @Test
  void getPropertyById_ShouldThrowException_WhenNotFound() {
    when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () -> propertyService.getPropertyById(1L)
    );

    verify(propertyRepository).findById(1L);
  }

  @Test
  void getPropertyById_ShouldReturnProperty_WhenExists() {
    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    var result = propertyService.getPropertyById(1L);

    assertNotNull(result);

    verify(propertyRepository).findById(1L);
  }

  @Test
  void deleteProperty_ShouldThrowException_WhenPropertyByIdAndHostNotFound() {
    when(propertyRepository.findByIdAndHostEmail(1L, host.getEmail()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class,
      () -> propertyService.deleteProperty(host, 1L)
    );

    verify(propertyRepository).findByIdAndHostEmail(1L, host.getEmail());
    verify(propertyRepository, never()).delete(any());
  }

  @Test
  void deleteProperty_ShouldDelete_WhenPropertyByIdAndHostExists() {
    when(propertyRepository.findByIdAndHostEmail(1L, host.getEmail()))
      .thenReturn(Optional.of(property));

    assertDoesNotThrow(() -> propertyService.deleteProperty(host, 1L));

    verify(propertyRepository).findByIdAndHostEmail(1L, host.getEmail());
    verify(propertyRepository).delete(property);
  }

  @Test
  void updateProperty_ShouldThrowException_WhenDuplicate() {
    when(propertyRepository.findByIdAndHostEmail(1L, host.getEmail()))
      .thenReturn(Optional.of(property));
    when(propertyRepository.existsByNameAndZipcodeAndIdNot(anyString(), anyString(), anyLong()))
      .thenReturn(true);

    assertThrows(
      BadRequestException.class, () -> propertyService.updateProperty(
        host,
        1L,
        updatePropertyRequest
      )
    );

    verify(propertyRepository).existsByNameAndZipcodeAndIdNot(anyString(), anyString(), anyLong());
    verify(propertyRepository, never()).save(any(Property.class));
  }

  @Test
  void updateProperty_ShouldUpdateProperty_WhenAllRequestFieldsAreNull() {
    when(propertyRepository.findByIdAndHostEmail(1L, host.getEmail()))
      .thenReturn(Optional.of(property));
    when(propertyRepository.existsByNameAndZipcodeAndIdNot(anyString(), anyString(), anyLong()))
      .thenReturn(false);
    when(propertyRepository.save(any(Property.class)))
      .thenReturn(property);
    when(imageRepository.saveAll(anyList()))
      .thenReturn(propertyImages);

    assertDoesNotThrow(
      () -> propertyService.updateProperty(
        host,
        1L,
        new UpdatePropertyRequest(
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null
        )
      )
    );

    verify(propertyRepository).existsByNameAndZipcodeAndIdNot(any(), any(), anyLong());
    verify(propertyRepository).save(any(Property.class));
    verify(imageRepository, never()).saveAll(anyList());
  }

  @Test
  void updateProperty_ShouldUpdateProperty_WhenNotDuplicate() {
    when(propertyRepository.findByIdAndHostEmail(1L, host.getEmail()))
      .thenReturn(Optional.of(property));
    when(propertyRepository.existsByNameAndZipcodeAndIdNot(anyString(), anyString(), anyLong()))
      .thenReturn(false);
    when(propertyRepository.save(any(Property.class)))
      .thenReturn(property);

    assertDoesNotThrow(
      () -> propertyService.updateProperty(
        host,
        1L,
        updatePropertyRequest
      )
    );

    verify(propertyRepository).existsByNameAndZipcodeAndIdNot(any(), any(), anyLong());
    verify(propertyRepository).save(any(Property.class));
  }

  @Test
  void getHostPropertyById_ShouldThrowException_WhenNotFound() {
    when(propertyRepository.findByIdAndHostEmail(anyLong(), anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () -> propertyService.getHostPropertyById(host, 1L)
    );

    verify(propertyRepository).findByIdAndHostEmail(anyLong(), anyString());
  }

  @Test
  void getHostPropertyById_ShouldReturnProperty_WhenExists() {
    when(propertyRepository.findByIdAndHostEmail(anyLong(), anyString()))
      .thenReturn(Optional.of(property));

    var result = propertyService.getHostPropertyById(host, 1L);

    assertNotNull(result);
    assertEquals(property.getId(), result.id());
    verify(propertyRepository).findByIdAndHostEmail(anyLong(), anyString());
  }

  @Test
  void searchProperties_ShouldReturnPageOfProperties() {
    Page<Property> propertyPage = new PageImpl<>(List.of(property));

    when(propertyRepository.searchProperties(
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyInt(),
      anyInt(),
      anyInt(),
      anyInt(),
      eq(new BigDecimal("1000")),
      eq(new BigDecimal("3000")),
      eq(new BigDecimal("80")),
      eq(new BigDecimal("110")),
      any(Pageable.class)
    ))
      .thenReturn(propertyPage);

    when(propertyRepository.queryProperties(anyString(), any(Pageable.class)))
      .thenReturn(propertyPage);

    var searchResult = propertyService.searchProperties(
      "",
      "name",
      "category",
      "street",
      "city",
      "zipcode",
      3,
      5,
      3,
      5,
      new BigDecimal("1000"),
      new BigDecimal("3000"),
      new BigDecimal("80"),
      new BigDecimal("110"),
      0,
      10
    );

    var queryResult = propertyService.searchProperties(
      "query",
      "name",
      "category",
      "street",
      "city",
      "zipcode",
      3,
      5,
      3,
      5,
      new BigDecimal("1000"),
      new BigDecimal("3000"),
      new BigDecimal("80"),
      new BigDecimal("110"),
      0,
      10
    );

    assertNotNull(searchResult);
    assertNotNull(queryResult);

    verify(propertyRepository).searchProperties(
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyInt(),
      anyInt(),
      anyInt(),
      anyInt(),
      eq(new BigDecimal("1000")),
      eq(new BigDecimal("3000")),
      eq(new BigDecimal("80")),
      eq(new BigDecimal("110")),
      any(Pageable.class)
    );
  }

  @Test
  void searchHostProperties_ShouldReturnPageOfProperties() {
    Page<Property> propertyPage = new PageImpl<>(List.of(property));

    when(propertyRepository.searchHostProperties(
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyInt(),
      anyInt(),
      anyInt(),
      anyInt(),
      eq(new BigDecimal("1000")),
      eq(new BigDecimal("3000")),
      eq(new BigDecimal("80")),
      eq(new BigDecimal("110")),
      any(Pageable.class)
    ))
      .thenReturn(propertyPage);

    when(propertyRepository.queryHostProperties(anyString(), anyString(), any(Pageable.class)))
      .thenReturn(propertyPage);

    var result1 = propertyService.searchHostProperties(
      host,
      "",
      "name",
      "category",
      "street",
      "city",
      "zipcode",
      3,
      5,
      3,
      5,
      new BigDecimal("1000"),
      new BigDecimal("3000"),
      new BigDecimal("80"),
      new BigDecimal("110"),
      0,
      10
    );

    assertNotNull(result1);

    verify(propertyRepository).searchHostProperties(
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyInt(),
      anyInt(),
      anyInt(),
      anyInt(),
      eq(new BigDecimal("1000")),
      eq(new BigDecimal("3000")),
      eq(new BigDecimal("80")),
      eq(new BigDecimal("110")),
      any(Pageable.class)
    );

    var result2 = propertyService.searchHostProperties(
      host,
      "query",
      "name",
      "category",
      "street",
      "city",
      "zipcode",
      3,
      5,
      3,
      5,
      new BigDecimal("1000"),
      new BigDecimal("3000"),
      new BigDecimal("80"),
      new BigDecimal("110"),
      0,
      10
    );

    assertNotNull(result2);

    verify(propertyRepository).queryHostProperties(
      anyString(),
      anyString(),
      any(Pageable.class)
    );
  }

  @Test
  void getRecentProperties_ShouldReturnList() {
    when(propertyRepository.findRecentProperties(anyInt()))
      .thenReturn(List.of(property));

    var result = propertyService.getRecentProperties(4);

    assertNotNull(result);

    verify(propertyRepository).findRecentProperties(4);
  }

  @Test
  void countProperties_ShouldReturnNumberOfProperties() {
    when(propertyRepository.count()).thenReturn(100L);

    var result = propertyService.countProperties();

    assertNotNull(result);
    assertEquals(100L, result);

    verify(propertyRepository).count();
  }

  @Test
  void getPropertyImages_ShouldThrowException_WhenInvalidRole() {
    assertThrows(
      AccessDeniedException.class, () ->
        propertyService.getPropertyImages(guest, property.getId(), 0, 10)
    );

    verify(imageRepository, never())
      .findAllByPropertyIdAndPropertyHostEmail(anyLong(), anyString(), any());
  }

  @Test
  void getPropertyImages_ShouldReturnPage() {
    when(imageRepository.findAllByPropertyIdAndPropertyHostEmail(
      any(),
      any(),
      any()
    )).thenReturn(new PageImpl<>(List.of(propertyImage)));

    var result = propertyService.getPropertyImages(host, property.getId(), 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    verify(imageRepository).findAllByPropertyIdAndPropertyHostEmail(any(), any(), any());
  }

  @Test
  void deleteImage_ShouldThrowException_WhenNotFound() {
    when(imageRepository.findByIdAndPropertyHostEmail(anyLong(), anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () ->
        propertyService.deleteImage(host, 99L)
    );

    verify(imageRepository, never()).delete(any());
  }

  @Test
  void deleteImage_ShouldDeleteImage_WhenExists() {
    when(imageRepository.findByIdAndPropertyHostEmail(anyLong(), anyString()))
      .thenReturn(Optional.of(propertyImage));

    assertDoesNotThrow(() -> propertyService.deleteImage(host, propertyImage.getId()));

    verify(imageRepository).delete(propertyImage);
  }

  @Test
  void addImagesToProperty_ShouldThrowException_WhenPropertyNotFound() {
    when(propertyRepository.findByIdAndHostEmail(anyLong(), anyString()))
      .thenReturn(Optional.empty());

    assertThrows(
      NotFoundException.class, () ->
        propertyService.addImagesToProperty(host, 99L, List.of(new ImageRequest("object-key")))
    );

    verify(imageRepository, never()).saveAll(any());
  }

  @Test
  void addImagesToProperty_ShouldAddImages_WhenPropertyExists() {
    when(propertyRepository.findByIdAndHostEmail(any(), any()))
      .thenReturn(Optional.of(property));

    propertyService.addImagesToProperty(
      host,
      property.getId(),
      List.of(new ImageRequest("object-key"))
    );

    verify(propertyRepository).findByIdAndHostEmail(anyLong(), anyString());
    verify(imageRepository).saveAll(anyList());
  }

}