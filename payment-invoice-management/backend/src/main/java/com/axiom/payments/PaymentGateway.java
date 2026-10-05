package com.axiom.payments;

import java.math.BigDecimal;

/** Abstraction: a production gateway must verify an external provider event server-side. */
public interface PaymentGateway {
  String pay(Long invoiceId, BigDecimal amount);
}
