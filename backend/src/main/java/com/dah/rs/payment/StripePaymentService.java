package com.dah.rs.payment;

import static com.stripe.param.checkout.SessionCreateParams.builder;

import com.dah.rs.booking.Booking;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.Mode;
import com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!test")
@Service
public class StripePaymentService implements PaymentService {

  @Value("${hs.stripe.secret.key}")
  private String secretKey;

  @Value("${hs.stripe.success.url}")
  private String successUrl;

  public PaymentResponse createPaymentLink(Booking booking) {
    Stripe.apiKey = secretKey;

    long bookingTotalInCents =
      booking.getOrderTotal().multiply(BigDecimal.valueOf(100)).longValue();

    SessionCreateParams params = builder()
      .addPaymentMethodType(PaymentMethodType.CARD)
      .setMode(Mode.PAYMENT)
      .setSuccessUrl(successUrl + '/' + booking.getId())
      .addLineItem(
        LineItem.builder()
          .setQuantity(1L)
          .setPriceData(
            LineItem.PriceData.builder()
              .setCurrency("usd")
              .setUnitAmount(bookingTotalInCents)
              .setProductData(
                LineItem.PriceData.ProductData.builder()
                  .setName("booking")
                  .build()
              )
              .build()
          )
          .build())
      .build();

    Session session;
    try {
      session = Session.create(params);
    } catch (StripeException e) {
      throw new RuntimeException(e);
    }

    return new PaymentResponse(session.getUrl());
  }

}
