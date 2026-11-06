package com.dah.rs.user;

import com.dah.rs.booking.Booking;
import com.dah.rs.common.AuditableEntity;
import com.dah.rs.property.Property;
import com.dah.rs.review.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "app_user")
public class AppUser extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(name = "cognito_sub", nullable = false, unique = true)
  private String cognitoSub;

  @Email
  @NotBlank
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private AppRole role;

  @NotNull
  @OneToMany(mappedBy = "host")
  @Builder.Default
  private List<Property> properties = new ArrayList<>();

  @NotNull
  @OneToMany(mappedBy = "guest")
  @Builder.Default
  private List<Booking> bookings = new ArrayList<>();

  @NotNull
  @OneToMany(mappedBy = "guest")
  @Builder.Default
  private List<Review> reviews = new ArrayList<>();

}
