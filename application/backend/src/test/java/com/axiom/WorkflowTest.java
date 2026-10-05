package com.axiom;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
  properties = {
    "spring.datasource.url=jdbc:h2:mem:axiom;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=validate",
    "axiom.demo=true",
    "axiom.mail.enabled=false",
    "axiom.upload-dir=target/test-uploads",
  }
)
@AutoConfigureMockMvc
class WorkflowTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper json;

  @Autowired
  com.axiom.users.AppUserRepository users;

  String account(String role) {
    return role.toLowerCase() + "@axiom.test";
  }

  String call(String role, String path, Object body) throws Exception {
    return mvc
      .perform(
        post("/api" + path)
          .with(httpBasic(account(role), "AxiomDemo123!"))
          .contentType("application/json")
          .content(json.writeValueAsString(body))
      )
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
  }

  long id(String body) throws Exception {
    return json.readTree(body).get("id").asLong();
  }

  @Test
  void adminDeletesAccountAndPreventsLogin() throws Exception {
    long user = id(mvc.perform(post("/api/users").with(httpBasic("admin", "admin@2026"))
      .contentType("application/json").content(json.writeValueAsString(Map.of("name", "Delete me", "email", "delete-me@axiom.test", "password", "LongPassword123!", "role", "DESIGNER"))))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    mvc.perform(delete("/api/users/" + user).with(httpBasic("admin", "admin@2026"))).andExpect(status().isOk());
    assertFalse(users.existsById(user), "Deleted account must be removed from the database");
    assertTrue(users.findByEmail("delete-me@axiom.test").isEmpty());
    mvc.perform(get("/api/users").with(httpBasic("admin", "admin@2026"))).andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("delete-me@axiom.test"))));
    mvc.perform(get("/api/auth/me").with(httpBasic("delete-me@axiom.test", "LongPassword123!"))).andExpect(status().isUnauthorized());
    long admin = id(mvc.perform(get("/api/auth/me").with(httpBasic("admin", "admin@2026"))).andReturn().getResponse().getContentAsString());
    mvc.perform(delete("/api/users/" + admin).with(httpBasic("admin", "admin@2026"))).andExpect(status().isBadRequest());
    assertTrue(users.existsById(admin));
  }

  @Test
  void deletingLinkedAccountPreservesAccountAndHistory() throws Exception {
    String email = "linked-delete@axiom.test";
    long user = id(mvc.perform(post("/api/users").with(httpBasic("admin", "admin@2026"))
      .contentType("application/json").content(json.writeValueAsString(Map.of("name", "Linked customer", "email", email, "password", "LongPassword123!", "role", "CUSTOMER"))))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    long message = id(mvc.perform(post("/api/communications").with(httpBasic(email, "LongPassword123!"))
      .contentType("application/json").content(json.writeValueAsString(Map.of("subject", "Keep history", "message", "Customer enquiry", "kind", "MESSAGE"))))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    mvc.perform(delete("/api/users/" + user).with(httpBasic("admin", "admin@2026")))
      .andExpect(status().isConflict());
    var account = users.findById(user).orElseThrow();
    assertTrue(account.active);
    assertFalse(account.deleted);
    mvc.perform(get("/api/users").with(httpBasic("admin", "admin@2026")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString(email)));
    var messages = json.readTree(mvc.perform(get("/api/communications").with(httpBasic(email, "LongPassword123!")))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    assertTrue(java.util.stream.StreamSupport.stream(messages.spliterator(), false)
      .anyMatch(row -> row.get("id").asLong() == message));
  }

  @Test
  void quotationPriceApprovalPdfAndDirectAcceptance() throws Exception {
    var offerings = json.readTree(mvc.perform(get("/api/catalog/public")).andReturn().getResponse().getContentAsString());
    long offering = offerings.get(0).get("id").asLong();
    var base = offerings.get(0).get("price").decimalValue();
    long quote = id(call("CUSTOMER", "/quotes", Map.of("title", "A brighter brand", "requirements", "A vibrant identity and launch campaign.", "items", new Object[]{Map.of("offeringId", offering, "quantity", 1)})));
    for (String role : new String[]{"CUSTOMER", "QUOTATION_STAFF", "FINANCE_MANAGER"}) {
      mvc.perform(get("/api/quotes/" + quote + "/document").with(httpBasic(account(role), "AxiomDemo123!"))).andExpect(status().isForbidden());
    }
    mvc.perform(post("/api/quotes/" + quote + "/accept").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!")).contentType("application/json").content("{}")).andExpect(status().isBadRequest());
    mvc.perform(post("/api/projects").with(httpBasic(account("PROJECT_MANAGER"), "AxiomDemo123!")).contentType("application/json").content("{\"quotationId\":"+quote+"}")).andExpect(status().isBadRequest());
    var update = Map.of("requirements", "A vibrant identity and launch campaign.", "additionalAmount", "2500.00", "additionalDescription", "Additional social media artwork");
    for (int n = 0; n < 2; n++) {
      var updated = mvc.perform(put("/api/quotes/" + quote).with(httpBasic(account("QUOTATION_STAFF"), "AxiomDemo123!")).contentType("application/json").content(json.writeValueAsString(update))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
      assertEquals(0, json.readTree(updated).get("total").decimalValue().compareTo(base.add(new java.math.BigDecimal("2500"))));
    }
    call("QUOTATION_STAFF", "/quotes/" + quote + "/generate", Map.of());
    mvc.perform(get("/api/quotes/" + quote + "/document").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isForbidden());
    call("FINANCE_MANAGER", "/quotes/" + quote + "/approve", Map.of());
    for (String role : new String[]{"CUSTOMER", "QUOTATION_STAFF", "FINANCE_MANAGER"}) {
      byte[] pdf = mvc.perform(get("/api/quotes/" + quote + "/document").with(httpBasic(account(role), "AxiomDemo123!")))
        .andExpect(status().isOk()).andExpect(content().contentType("application/pdf")).andReturn().getResponse().getContentAsByteArray();
      assertTrue(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-"));
      var reader = new com.lowagie.text.pdf.PdfReader(pdf);
      assertTrue(reader.getNumberOfPages() >= 1); reader.close();
      java.nio.file.Files.write(java.nio.file.Path.of("target/quotation-preview.pdf"), pdf);
    }
    mvc.perform(get("/api/quotes/" + quote + "/document").with(httpBasic(account("PROJECT_MANAGER"), "AxiomDemo123!"))).andExpect(status().isForbidden());
    // A separate customer cannot access someone else's approved quotation.
    mvc.perform(post("/api/auth/register").contentType("application/json").content(json.writeValueAsString(Map.of("name", "Other client", "email", "other-client@axiom.test", "password", "LongPassword123!")))).andExpect(status().isOk());
    mvc.perform(get("/api/quotes/" + quote + "/document").with(httpBasic("other-client@axiom.test", "LongPassword123!"))).andExpect(status().isForbidden());
    call("CUSTOMER", "/quotes/" + quote + "/accept", Map.of());
    call("FINANCE_MANAGER", "/quotes/" + quote + "/send", Map.of());
    call("PROJECT_MANAGER", "/projects", Map.of("quotationId", quote));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(strings = {"VISA", "MASTERCARD", "BANK_TRANSFER"})
  void fullApprovalWorkflowAndDownloadGate(String paymentMethod) throws Exception {
    long customerId = id(mvc.perform(get("/api/auth/me").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andReturn().getResponse().getContentAsString());
    mvc.perform(put("/api/users/" + customerId).with(httpBasic("admin", "admin@2026"))
      .contentType("application/json").content(json.writeValueAsString(Map.of("name", "CUSTOMER", "role", "CUSTOMER", "active", true,
        "capacity", 3, "address", "15 Lake Road, Colombo", "contactNo", "+94 77 123 4567", "sex", "F", "birthDate", "2000-05-12")))).andExpect(status().isOk());
    String offerings = mvc
      .perform(get("/api/catalog/public"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    long offering = json.readTree(offerings).get(0).get("id").asLong();
    long quotation = id(
      call(
        "CUSTOMER",
        "/quotes",
        Map.of(
          "title",
          "Test project",
          "requirements",
          "Logo and website",
          "items",
          new Object[] { Map.of("offeringId", offering, "quantity", 1) }
        )
      )
    );
    mvc
      .perform(
        post("/api/quotes/" + quotation + "/approve")
          .with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))
          .contentType("application/json")
          .content("{}")
      )
      .andExpect(status().isForbidden());
    call("QUOTATION_STAFF", "/quotes/" + quotation + "/generate", Map.of());
    call(
      "FINANCE_MANAGER",
      "/quotes/" + quotation + "/return",
      Map.of("feedback", "Clarify deliverables")
    );
    call("QUOTATION_STAFF", "/quotes/" + quotation + "/generate", Map.of());
    call("FINANCE_MANAGER", "/quotes/" + quotation + "/approve", Map.of());
    call("FINANCE_MANAGER", "/quotes/" + quotation + "/send", Map.of());
    call("CUSTOMER", "/quotes/" + quotation + "/accept", Map.of());
    long project = id(
      call("PROJECT_MANAGER", "/projects", Map.of("quotationId", quotation))
    );
    String designers = mvc
      .perform(
        get("/api/users/designers").with(
          httpBasic(account("PROJECT_MANAGER"), "AxiomDemo123!")
        )
      )
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    long designer = json.readTree(designers).get(0).get("id").asLong();
    mvc
      .perform(
        put("/api/projects/" + project + "/team")
          .with(httpBasic(account("PROJECT_MANAGER"), "AxiomDemo123!"))
          .contentType("application/json")
          .content("{\"designers\":[" + designer + "]}")
      )
      .andExpect(status().isOk());
    mvc
      .perform(
        multipart("/api/projects/" + project + "/assets")
          .file(
            new MockMultipartFile(
              "file",
              "prototype.png",
              "image/png",
              new byte[] { 1, 2, 3 }
            )
          )
          .param("kind", "PROTOTYPE")
          .with(httpBasic(account("DESIGNER"), "AxiomDemo123!"))
      )
      .andExpect(status().isOk());
    call(
      "CUSTOMER",
      "/projects/" + project + "/review",
      Map.of("approved", true, "message", "Looks good")
    );
    call(
      "PROJECT_MANAGER",
      "/projects/" + project + "/review",
      Map.of("approved", true, "message", "Private design notes")
    );
    String customerFeedback = mvc
      .perform(
        get("/api/projects/" + project + "/feedback").with(
          httpBasic(account("CUSTOMER"), "AxiomDemo123!")
        )
      )
      .andReturn()
      .getResponse()
      .getContentAsString();
    assertFalse(customerFeedback.contains("Private design notes"));
    long asset = id(
      mvc
        .perform(
          multipart("/api/projects/" + project + "/assets")
            .file(
              new MockMultipartFile(
                "file",
                "original.pdf",
                "application/pdf",
                "original".getBytes()
              )
            )
            .param("kind", "ORIGINAL")
            .with(httpBasic(account("DESIGNER"), "AxiomDemo123!"))
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString()
    );
    mvc
      .perform(
        get("/api/projects/assets/" + asset + "/download").with(
          httpBasic(account("CUSTOMER"), "AxiomDemo123!")
        )
      )
      .andExpect(status().isForbidden());
    // Final upload alone must not complete the project or create an invoice.
    mvc.perform(post("/api/projects/" + project + "/complete").with(httpBasic(account("PROJECT_MANAGER"), "AxiomDemo123!"))
      .contentType("application/json").content("{}")).andExpect(status().isBadRequest());
    mvc.perform(put("/api/projects/" + project + "/progress").with(httpBasic(account("DESIGNER"), "AxiomDemo123!"))
      .contentType("application/json").content("{\"progress\":100}")).andExpect(status().isOk());
    mvc.perform(post("/api/projects/" + project + "/complete").with(httpBasic(account("DESIGNER"), "AxiomDemo123!"))
      .contentType("application/json").content("{}")).andExpect(status().isForbidden());
    call("PROJECT_MANAGER", "/projects/" + project + "/complete", Map.of());
    var invoiceList = json.readTree(mvc.perform(get("/api/invoices").with(httpBasic(account("FINANCE_STAFF"), "AxiomDemo123!")))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    long invoice = 0;
    for (var item : invoiceList) if (item.get("projectId").asLong() == project) invoice = item.get("id").asLong();
    assertTrue(invoice > 0);
    for (var item : invoiceList) if (item.get("id").asLong() == invoice) {
      assertEquals("15 Lake Road, Colombo", item.get("billingAddress").asText());
      assertEquals("+94 77 123 4567", item.get("contactNo").asText());
    }
    mvc.perform(delete("/api/invoices/" + invoice).with(httpBasic(account("FINANCE_STAFF"), "AxiomDemo123!"))).andExpect(status().isOk());
    invoice = id(call("FINANCE_STAFF", "/invoices", Map.of("projectId", project, "billingName", "Initial customer", "billingAddress", "Initial address")));
    var drafts = mvc.perform(get("/api/invoices").with(httpBasic(account("FINANCE_STAFF"), "AxiomDemo123!")))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    assertTrue(drafts.contains("Initial address"));

    mvc.perform(get("/api/invoices/" + invoice + "/document").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isForbidden());
    mvc.perform(post("/api/invoices/" + invoice + "/generate").with(httpBasic(account("FINANCE_MANAGER"), "AxiomDemo123!"))
      .contentType("application/json").content("{}")).andExpect(status().isBadRequest());
    mvc.perform(put("/api/invoices/" + invoice).with(httpBasic(account("FINANCE_STAFF"), "AxiomDemo123!"))
      .contentType("application/json").content("{\"billingName\":\"Customer\",\"billingAddress\":\"Colombo\"}")).andExpect(status().isOk());
    call("FINANCE_STAFF", "/invoices/" + invoice + "/approve", Map.of());
    call("FINANCE_MANAGER", "/invoices/" + invoice + "/return", Map.of("feedback", "Check address"));
    call("FINANCE_STAFF", "/invoices/" + invoice + "/approve", Map.of());
    call("FINANCE_MANAGER", "/invoices/" + invoice + "/generate", Map.of());
    byte[] invoicePdf = mvc.perform(get("/api/invoices/" + invoice + "/document").with(httpBasic(account("FINANCE_MANAGER"), "AxiomDemo123!")))
      .andExpect(status().isOk()).andExpect(content().contentType("application/pdf")).andReturn().getResponse().getContentAsByteArray();
    var invoiceReader = new com.lowagie.text.pdf.PdfReader(invoicePdf);
    assertTrue(new com.lowagie.text.pdf.parser.PdfTextExtractor(invoiceReader).getTextFromPage(1).contains("Colombo"));
    assertTrue(new com.lowagie.text.pdf.parser.PdfTextExtractor(invoiceReader).getTextFromPage(1).contains("+94 77 123 4567"));
    invoiceReader.close();
    java.nio.file.Files.write(java.nio.file.Path.of("target/invoice-preview.pdf"), invoicePdf);
    call("FINANCE_MANAGER", "/invoices/" + invoice + "/send", Map.of());
    mvc.perform(get("/api/projects/assets/" + asset + "/download").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isForbidden());
    mvc.perform(post("/api/invoices/" + invoice + "/pay").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))
      .contentType("application/json").content("{\"paymentMethod\":\"INVALID\"}")).andExpect(status().isBadRequest());
    call("CUSTOMER", "/invoices/" + invoice + "/pay", Map.of("paymentMethod", paymentMethod));
    mvc.perform(post("/api/invoices/" + invoice + "/pay").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))
      .contentType("application/json").content("{\"paymentMethod\":\"DEMO_CARD\"}")).andExpect(status().isBadRequest());
    for (var role : new String[]{"CUSTOMER", "FINANCE_STAFF", "FINANCE_MANAGER"}) {
      byte[] receiptPdf = mvc.perform(get("/api/invoices/" + invoice + "/receipt").with(httpBasic(account(role), "AxiomDemo123!")))
        .andExpect(status().isOk()).andExpect(content().contentType("application/pdf")).andReturn().getResponse().getContentAsByteArray();
      var receiptReader = new com.lowagie.text.pdf.PdfReader(receiptPdf);
      assertTrue(new com.lowagie.text.pdf.parser.PdfTextExtractor(receiptReader).getTextFromPage(1).contains(paymentMethod));
      receiptReader.close();
      java.nio.file.Files.write(java.nio.file.Path.of("target/receipt-preview.pdf"), receiptPdf);
      mvc.perform(get("/api/communications/outbox").with(httpBasic(account(role), "AxiomDemo123!")))
        .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Payment receipt #" + invoice)));
    }
    mvc.perform(delete("/api/invoices/" + invoice).with(httpBasic(account("FINANCE_STAFF"), "AxiomDemo123!"))).andExpect(status().isBadRequest());
    mvc.perform(delete("/api/invoices/" + invoice).with(httpBasic(account("FINANCE_MANAGER"), "AxiomDemo123!"))).andExpect(status().isOk());
    mvc.perform(get("/api/invoices/" + invoice + "/receipt").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isOk());

    mvc.perform(get("/api/projects/assets/" + asset + "/download").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isOk());
  }

  @Test
  void registrationStoresAndValidatesProfileDetails() throws Exception {
    var profile = new java.util.HashMap<String, Object>(Map.of("name", "Profile customer", "email", "profile@axiom.test", "password", "LongPassword123!",
      "address", "15 Lake Road, Colombo", "contactNo", "+94 77 123 4567", "sex", "F", "birthDate", "2000-05-12"));
    mvc.perform(post("/api/auth/register").contentType("application/json").content(json.writeValueAsString(profile)))
      .andExpect(status().isOk()).andExpect(jsonPath("$.address").value("15 Lake Road, Colombo"))
      .andExpect(jsonPath("$.contactNo").value("+94 77 123 4567")).andExpect(jsonPath("$.sex").value("F"))
      .andExpect(jsonPath("$.birthDate").value("2000-05-12"));
    profile.put("email", "invalid-profile@axiom.test"); profile.put("birthDate", "2999-01-01");
    mvc.perform(post("/api/auth/register").contentType("application/json").content(json.writeValueAsString(profile))).andExpect(status().isBadRequest());
    profile.put("birthDate", "2000-05-12"); profile.put("sex", "invalid");
    mvc.perform(post("/api/auth/register").contentType("application/json").content(json.writeValueAsString(profile))).andExpect(status().isBadRequest());
  }

  @Test
  void publicRegistrationCannotGrantAdmin() throws Exception {
    mvc
      .perform(
        post("/api/auth/register")
          .contentType("application/json")
          .content(
            "{\"name\":\"Visitor\",\"email\":\"visitor@example.test\",\"password\":\"LongPassword123!\",\"role\":\"ADMIN\"}"
          )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.role").value("CUSTOMER"))
      .andExpect(jsonPath("$.passwordHash").doesNotExist());
    mvc
      .perform(
        get("/api/users").with(
          httpBasic("visitor@example.test", "LongPassword123!")
        )
      )
      .andExpect(status().isForbidden());
  }

  @Test
  void adminUsernameUsesRequestedPassword() throws Exception {
    mvc.perform(get("/api/auth/me").with(httpBasic("admin", "admin@2026")))
      .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("ADMIN"));
    mvc.perform(get("/api/users").with(httpBasic("admin", "admin@2026")))
      .andExpect(status().isOk());
    mvc.perform(get("/api/auth/me").with(httpBasic("admin", "wrong-password")))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void catalogueCrudAndPublicationGate() throws Exception {
    var offering = Map.of("name", "Viva offering", "kind", "Service", "description", "Test design", "deliverables", "One design", "price", 15000);
    long offeringId = id(call("SERVICE_STAFF", "/catalog", offering));
    mvc.perform(get("/api/catalog/public")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Viva offering"))));
    mvc.perform(put("/api/catalog/" + offeringId).with(httpBasic(account("SERVICE_STAFF"), "AxiomDemo123!")).contentType("application/json").content(json.writeValueAsString(offering))).andExpect(status().isOk());
    call("SERVICE_STAFF", "/catalog/" + offeringId + "/submit", Map.of());
    mvc.perform(post("/api/catalog/" + offeringId + "/approve").with(httpBasic(account("SERVICE_STAFF"), "AxiomDemo123!")).contentType("application/json").content("{}")).andExpect(status().isForbidden());
    call("MARKETING_MANAGER", "/catalog/" + offeringId + "/approve", Map.of());
    mvc.perform(get("/api/catalog/public")).andExpect(content().string(org.hamcrest.Matchers.containsString("Viva offering")));
    call("MARKETING_MANAGER", "/catalog/" + offeringId + "/return", Map.of("feedback", "Retire this practice offering"));
    mvc.perform(delete("/api/catalog/" + offeringId).with(httpBasic(account("SERVICE_STAFF"), "AxiomDemo123!"))).andExpect(status().isOk());
  }

  @Test
  void customerConversationKeepsRepliesUntilStaffMarksSolved() throws Exception {
    long conversation = id(call("CUSTOMER", "/communications", Map.of("subject", "Payment question", "message", "How do I pay?", "kind", "MESSAGE")));
    String path = "/api/communications/" + conversation;
    mvc.perform(delete(path).with(httpBasic(account("COMMUNICATION_STAFF"), "AxiomDemo123!"))).andExpect(status().isForbidden());
    mvc.perform(post(path + "/reply").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))
      .contentType("application/json").content("{\"reply\":\"Any update?\"}")).andExpect(status().isBadRequest());
    call("COMMUNICATION_STAFF", "/communications/" + conversation + "/reply", Map.of("reply", "Select your invoice"));
    call("CUSTOMER", "/communications/" + conversation + "/reply", Map.of("reply", "Can I use bank transfer?"));
    var updated = json.readTree(call("COMMUNICATION_STAFF", "/communications/" + conversation + "/reply", Map.of("reply", "Yes, choose demo bank transfer")));
    assertEquals(3, updated.get("replies").size());
    assertEquals("Select your invoice", updated.get("replies").get(0).get("body").asText());
    assertEquals("CUSTOMER", updated.get("replies").get(1).get("authorRole").asText());
    mvc.perform(post("/api/auth/register").contentType("application/json").content(json.writeValueAsString(
      Map.of("name", "Unrelated customer", "email", "unrelated-message@axiom.test", "password", "LongPassword123!")))).andExpect(status().isOk());
    mvc.perform(post(path + "/reply").with(httpBasic("unrelated-message@axiom.test", "LongPassword123!"))
      .contentType("application/json").content("{\"reply\":\"Not my conversation\"}")).andExpect(status().isForbidden());
    mvc.perform(post(path + "/solve").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))
      .contentType("application/json").content("{}")).andExpect(status().isForbidden());
    var solved = json.readTree(call("COMMUNICATION_STAFF", "/communications/" + conversation + "/solve", Map.of()));
    assertEquals("SOLVED", solved.get("status").asText());
    assertEquals(3, solved.get("replies").size());
    for (String role : new String[]{"CUSTOMER", "COMMUNICATION_STAFF"}) {
      mvc.perform(post(path + "/reply").with(httpBasic(account(role), "AxiomDemo123!"))
        .contentType("application/json").content("{\"reply\":\"Late reply\"}")).andExpect(status().isBadRequest());
    }
  }

  @Test
  void marketingManagerDeletesPublishedServiceAndPackage() throws Exception {
    for (String kind : new String[]{"Service", "Package"}) {
      long offering = id(call("SERVICE_STAFF", "/catalog", Map.of("name", "Remove " + kind, "kind", kind,
        "description", "Temporary offering", "deliverables", "Design", "price", 1000)));
      call("SERVICE_STAFF", "/catalog/" + offering + "/submit", Map.of());
      call("MARKETING_MANAGER", "/catalog/" + offering + "/approve", Map.of());
      long quote = id(call("CUSTOMER", "/quotes", Map.of("title", "Keep quotation history", "requirements", "Snapshot pricing",
        "items", new Object[]{Map.of("offeringId", offering, "quantity", 1)})));
      mvc.perform(delete("/api/catalog/" + offering).with(httpBasic(account("SERVICE_STAFF"), "AxiomDemo123!"))).andExpect(status().isBadRequest());
      mvc.perform(delete("/api/catalog/" + offering).with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isForbidden());
      mvc.perform(delete("/api/catalog/" + offering).with(httpBasic(account("MARKETING_MANAGER"), "AxiomDemo123!"))).andExpect(status().isOk());
      var publicItems = json.readTree(mvc.perform(get("/api/catalog/public")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
      for (var item : publicItems) assertNotEquals(offering, item.get("id").asLong());
      call("QUOTATION_STAFF", "/quotes/" + quote + "/generate", Map.of());
    }
  }

  @Test
  void consultationCrudAndOfficerApproval() throws Exception {
    var request = Map.of("subject", "Viva consultation", "message", "Discuss our campaign", "kind", "CONSULTATION", "appointmentAt", "2030-10-15T10:30");
    long conversation = id(call("CUSTOMER", "/communications", request));
    mvc.perform(put("/api/communications/" + conversation).with(httpBasic(account("CUSTOMER"), "AxiomDemo123!")).contentType("application/json").content(json.writeValueAsString(request))).andExpect(status().isOk());
    mvc.perform(post("/api/communications/" + conversation + "/confirm").with(httpBasic(account("COMMUNICATION_STAFF"), "AxiomDemo123!")).contentType("application/json").content("{}")).andExpect(status().isForbidden());
    call("CUSTOMER_RELATIONS_OFFICER", "/communications/" + conversation + "/confirm", Map.of());
    call("CUSTOMER_RELATIONS_OFFICER", "/communications/" + conversation + "/reply", Map.of("reply", "Confirmed, see you then"));
    mvc.perform(get("/api/communications").with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Confirmed, see you then")));
    long message = id(call("CUSTOMER", "/communications", Map.of("subject", "Temporary", "message", "Practice delete", "kind", "MESSAGE")));
    mvc.perform(delete("/api/communications/" + message).with(httpBasic(account("CUSTOMER"), "AxiomDemo123!"))).andExpect(status().isOk());
  }
}
