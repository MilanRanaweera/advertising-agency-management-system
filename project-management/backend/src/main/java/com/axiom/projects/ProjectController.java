package com.axiom.projects;

import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService s;
  private final AssetService assets;

  public ProjectController(ProjectService s, AssetService a) {
    this.s = s;
    assets = a;
  }

  public record Start(Long quotationId) {}

  public record Team(List<Long> designers) {}

  public record Progress(int progress) {}

  public record Review(boolean approved, String message) {}

  @GetMapping
  List<Project> list() {
    return s.list();
  }

  @PostMapping
  Project create(@RequestBody Start b) {
    return s.create(b.quotationId());
  }

  @PutMapping("/{id}/team")
  Project assign(@PathVariable Long id, @RequestBody Team b) {
    return s.assign(id, b.designers());
  }

  @GetMapping("/{id}/team")
  List<Assignment> team(@PathVariable Long id) {
    return s.team(id);
  }

  @PutMapping("/{id}/progress")
  Project progress(@PathVariable Long id, @RequestBody Progress b) {
    return s.progress(id, b.progress());
  }

  @PostMapping("/{id}/review")
  Project review(@PathVariable Long id, @RequestBody Review b) {
    return s.review(id, b.approved(), b.message());
  }

  @GetMapping("/{id}/feedback")
  List<ProjectFeedback> feedback(@PathVariable Long id) {
    return s.feedback(id);
  }

  @PostMapping("/{id}/complete")
  Project complete(@PathVariable Long id) {
    return s.complete(id);
  }

  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    s.delete(id);
  }

  @GetMapping("/{id}/assets")
  List<Asset> listAssets(@PathVariable Long id) {
    return assets.list(id);
  }

  @PostMapping("/{id}/assets")
  Asset upload(
    @PathVariable Long id,
    @RequestParam String kind,
    @RequestParam MultipartFile file
  ) throws Exception {
    return assets.upload(id, kind, file);
  }

  @GetMapping("/assets/{id}/download")
  ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
    var a = assets.allowed(id);
    return ResponseEntity.ok()
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .header(
        "Content-Disposition",
        ContentDisposition.attachment()
          .filename(a.originalName)
          .build()
          .toString()
      )
      .header("X-Content-Type-Options", "nosniff")
      .body(assets.bytes(a));
  }
}
