package com.axiom.core;

public final class Documents {

  private Documents() {}

  public static String escape(Object s) {
    return String.valueOf(s == null ? "" : s)
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;");
  }

  public static String render(
    String type,
    Long id,
    String title,
    String details,
    java.math.BigDecimal total,
    String customer
  ) {
    return (
      "<!doctype html><html><head><meta charset='utf-8'><title>" +
      type +
      " " +
      id +
      "</title><style>body{font:16px Arial;margin:55px;color:#152438}header{border-bottom:3px solid #216c91;padding-bottom:20px}h1{font-size:32px}pre{white-space:pre-wrap;font:inherit;line-height:1.7}.total{font-size:24px;margin-top:30px}</style></head><body><header><h1>AXIOM STUDIO</h1><p>Creative agency · Colombo, Sri Lanka<br>studio@example.test · +94 11 000 0000 (replace sample details)</p></header><h2>" +
      type +
      " #" +
      id +
      "</h2><p>Date: " +
      java.time.LocalDate.now() +
      "<br>Customer: " +
      escape(customer) +
      "</p><h3>" +
      escape(title) +
      "</h3><pre>" +
      escape(details) +
      "</pre><p class='total'>Total: LKR " +
      total +
      "</p><p>Thank you for working with AXIOM.</p></body></html>"
    );
  }
}
