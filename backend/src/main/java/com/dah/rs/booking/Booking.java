package com.dah.rs.booking;

import com.dah.rs.common.AuditableEntity;
import com.dah.rs.property.Property;
import com.dah.rs.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "booking")
public class Booking extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "property_id", nullable = false)
  private Property property;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "guest_id", nullable = false)
  private AppUser guest;

  @NotNull
  @Column(name = "check_in", nullable = false)
  private LocalDate checkIn;

  @NotNull
  @Column(name = "check_out", nullable = false)
  private LocalDate checkOut;

  @NotNull
  @DecimalMin("0.00")
  @Digits(integer = 10, fraction = 2)
  @Column(name = "order_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal orderTotal;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "booking_status", nullable = false)
  @Builder.Default
  private BookingStatus bookingStatus = BookingStatus.PENDING;

}
