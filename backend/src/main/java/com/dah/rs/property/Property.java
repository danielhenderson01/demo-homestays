package com.dah.rs.property;

import com.dah.rs.booking.Booking;
import com.dah.rs.common.AuditableEntity;
import com.dah.rs.image.PropertyImage;
import com.dah.rs.review.Review;
import com.dah.rs.user.AppUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "property")
public class Property extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "host_id", nullable = false)
  private AppUser host;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  @Builder.Default
  private Category category = Category.OTHER;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank
  @Column(name = "tagline", nullable = false)
  private String tagline;

  @NotBlank
  @Column(name = "description", nullable = false, columnDefinition = "text")
  private String description;

  @NotBlank
  @Column(name = "street", nullable = false)
  private String street;

  @NotBlank
  @Column(name = "city", nullable = false)
  private String city;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  private USState state;

  @NotBlank
  @Column(name = "zipcode", nullable = false)
  private String zipcode;

  @NotNull
  @PositiveOrZero
  @Column(name = "beds", nullable = false)
  private Integer beds;

  @NotNull
  @PositiveOrZero
  @Column(name = "baths", nullable = false)
  private Integer baths;

  @NotNull
  @Positive
  @Column(name = "square_feet", nullable = false)
  private Integer squareFeet;

  @NotNull
  @DecimalMin("0.00")
  @Digits(integer = 10, fraction = 2)
  @Column(name = "price", nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @ElementCollection
  @CollectionTable
  @Builder.Default
  private Set<Amenity> amenities = new HashSet<>();

  @NotNull
  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PropertyImage> images = new ArrayList<>();

  @NotNull
  @OneToMany(mappedBy = "property")
  @Builder.Default
  private List<Booking> bookings = new ArrayList<>();

  @NotNull
  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Review> reviews = new ArrayList<>();

}
