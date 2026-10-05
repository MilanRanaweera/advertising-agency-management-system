package com.axiom.payments;

import com.axiom.core.Rules;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SandboxGateway implements PaymentGateway {

  @Value("${axiom.demo:false}")
  private boolean enabled;

  @Override
  public String pay(Long id, BigDecimal amount) {
    Rules.require(
      enabled,
      "Sandbox disabled. A verified payment-provider integration is required."
    );
    return "SANDBOX-" + id + "-" + java.util.UUID.randomUUID();
  }
}
