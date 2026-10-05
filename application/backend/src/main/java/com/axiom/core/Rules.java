package com.axiom.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Rules {

  private Rules() {}

  public static void require(boolean ok, String message) {
    if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }

  public static void access(boolean ok) {
    if (!ok) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "This account cannot access this record or action."
    );
  }

  public static <T> T found(java.util.Optional<T> value) {
    return value.orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found")
    );
  }

  public static String text(String value) {
    require(value != null && !value.isBlank(), "Required text is missing");
    require(value.length() <= 10000, "Text is too long");
    return value.trim();
  }
}
