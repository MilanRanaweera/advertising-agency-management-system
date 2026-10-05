package com.axiom.catalog;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

  private final CatalogService service;

  public CatalogController(CatalogService s) {
    service = s;
  }

  @GetMapping("/public")
  List<Offering> published() {
    return service.published();
  }

  @GetMapping
  List<Offering> all() {
    return service.all();
  }

  @PostMapping
  Offering create(@RequestBody Offering o) {
    return service.save(null, o);
  }

  @PutMapping("/{id}")
  Offering update(@PathVariable Long id, @RequestBody Offering o) {
    return service.save(id, o);
  }

  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PostMapping("/{id}/{action}")
  Offering action(
    @PathVariable Long id,
    @PathVariable String action,
    @RequestBody Map<String, String> body
  ) {
    return service.action(id, action, body.get("feedback"));
  }
}
