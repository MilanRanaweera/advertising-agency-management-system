package com.axiom.core;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class Errors {

  @ExceptionHandler(
    {
      DataIntegrityViolationException.class,
      ObjectOptimisticLockingFailureException.class,
    }
  )
  ResponseEntity<?> conflict(Exception e) {
    return ResponseEntity.status(409).body(
      Map.of(
        "message",
        "Record is in use, already exists, or changed. Reload and try again."
      )
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<?> invalid(Exception e) {
    return ResponseEntity.badRequest().body(
      Map.of("message", "Invalid field value")
    );
  }
}
