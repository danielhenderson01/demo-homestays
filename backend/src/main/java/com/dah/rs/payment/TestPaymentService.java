package com.dah.rs.payment;

import com.dah.rs.booking.Booking;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class TestPaymentService implements PaymentService {

  @Override
  public PaymentResponse createPaymentLink(Booking booking) {
    return new PaymentResponse("payment-url");
  }

}
