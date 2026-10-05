package com.axiom.core;

import com.axiom.users.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final JavaMailSender sender;
  private final OutboxRepository repo;
  private final AppUserRepository users;

  @Value("${axiom.mail.enabled:false}")
  boolean enabled;

  @Value("${axiom.mail.from}")
  String from;

  public MailService(
    JavaMailSender s,
    OutboxRepository r,
    AppUserRepository u
  ) {
    sender = s;
    repo = r;
    users = u;
  }

  public void deliver(Long user, String subject, String html) {
    deliver(user, subject, html, null, null);
  }

  public void deliver(Long user, String subject, String html, byte[] attachment, String filename) {
    var mail = new OutboxMail();
    mail.recipient = Rules.found(users.findById(user)).email;
    mail.subject = subject;
    mail.body = html;
    mail.status = "LOCAL_OUTBOX";
    if (enabled) {
      try {
        var mime = sender.createMimeMessage();
        var helper = new MimeMessageHelper(mime, attachment != null, "UTF-8");
        helper.setFrom(from);
        helper.setTo(mail.recipient);
        helper.setSubject(subject);
        helper.setText(html, true);
        if (attachment != null) helper.addAttachment(filename, new org.springframework.core.io.ByteArrayResource(attachment), "application/pdf");
        sender.send(mime);
        mail.status = "SENT";
      } catch (Exception e) {
        throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.BAD_GATEWAY,
          "Email delivery failed; check SMTP and try again"
        );
      }
    }
    repo.save(mail);
  }
}
