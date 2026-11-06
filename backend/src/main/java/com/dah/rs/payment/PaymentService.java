package com.dah.rs.payment;

import com.dah.rs.booking.Booking;

public interface PaymentService {

  PaymentResponse createPaymentLink(Booking booking);

}
