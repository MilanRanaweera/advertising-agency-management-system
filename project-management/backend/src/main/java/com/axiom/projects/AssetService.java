package com.axiom.projects;

import com.axiom.core.*;
import com.axiom.payments.*;
import com.axiom.quotes.*;
import com.axiom.users.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AssetService {

  private final ProjectService projects;
  private final ProjectRepository projectRepo;
  private final AssetRepository assets;
  private final InvoiceRepository invoices;
  private final QuotationRepository quotes;
  private final Access access;
  private final Path folder;

  public AssetService(
    ProjectService p,
    ProjectRepository pr,
    AssetRepository a,
    InvoiceRepository i,
    QuotationRepository q,
    Access x,
    @Value("${axiom.upload-dir}") String dir
  ) throws java.io.IOException {
    projects = p;
    projectRepo = pr;
    assets = a;
    invoices = i;
    quotes = q;
    access = x;
    folder = Paths.get(dir).toAbsolutePath().normalize();
    Files.createDirectories(folder);
  }

  public List<Asset> list(Long project) {
    projects.get(project);
    return assets.findByProjectId(project);
  }

  @Transactional
  public Asset upload(Long project, String kind, MultipartFile file)
    throws java.io.IOException {
    access.role("DESIGNER");
    var p = projects.get(project);
    Rules.require(
      Set.of("PROTOTYPE", "ORIGINAL").contains(kind),
      "Invalid asset kind"
    );
    Rules.require(
      file.getSize() > 0 && file.getSize() <= 10485760,
      "File must be 1 byte to 10 MB"
    );
    Rules.require(
      !Set.of(
        "PLANNED",
        "COMPLETED",
        "CLIENT_REVIEW",
        "DIRECTOR_REVIEW"
      ).contains(p.status),
      "Project not ready for upload"
    );
    String type = Objects.toString(file.getContentType(), "");
    Rules.require(
      Set.of(
        "image/png",
        "image/jpeg",
        "application/pdf",
        "application/zip",
        "application/x-zip-compressed"
      ).contains(type),
      "Use PNG, JPEG, PDF or ZIP"
    );
    if (kind.equals("ORIGINAL")) {
      Rules.require(p.prototypeApproved, "Prototype must be approved first");
      p.status = "FINAL_REVIEW";
    } else {
      Rules.require(type.startsWith("image/"), "Prototype must be PNG or JPEG");
      p.status = "CLIENT_REVIEW";
      p.prototypeApproved = false;
      p.clientApproved = false;
    }
    var a = new Asset();
    a.projectId = project;
    a.kind = kind;
    a.originalName = Objects.toString(
      file.getOriginalFilename(),
      "design"
    ).replaceAll("[^a-zA-Z0-9._-]", "_");
    a.storageName = UUID.randomUUID().toString();
    a.contentType = type;
    Files.copy(file.getInputStream(), folder.resolve(a.storageName));
    projectRepo.save(p);
    return assets.save(a);
  }

  public Asset allowed(Long id) {
    var a = Rules.found(assets.findById(id));
    var p = projects.get(a.projectId);
    if ("ORIGINAL".equals(a.kind) && access.is("CUSTOMER")) {
      var total = Rules.found(quotes.findById(p.quotationId)).total;
      var paid = invoices
        .findAll()
        .stream()
        .filter(i -> i.projectId.equals(p.id) && "PAID".equals(i.status))
        .map(i -> i.amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
      Rules.access(p.prototypeApproved && paid.compareTo(total) >= 0);
    }
    return a;
  }

  public byte[] bytes(Asset a) throws java.io.IOException {
    return Files.readAllBytes(folder.resolve(a.storageName));
  }
}
