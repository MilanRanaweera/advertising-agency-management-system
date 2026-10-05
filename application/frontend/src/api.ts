import { Injectable } from "@angular/core";
export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  active: boolean;
  capacity: number;
}
@Injectable({ providedIn: "root" })
export class Api {
  user: User | null = null;
  private credentials = "";
  cart: { offeringId: number; quantity: number; name: string }[] = [];
  async login(email: string, password: string) {
    this.credentials =
      "Basic " + btoa(unescape(encodeURIComponent(email + ":" + password)));
    try {
      this.user = await this.get("/auth/me");
    } catch (e) {
      this.credentials = "";
      throw e;
    }
  }
  logout() {
    this.credentials = "";
    this.user = null;
    this.cart = [];
  }
  async request(path: string, method = "GET", body?: unknown): Promise<any> {
    const response = await fetch("/api" + path, {
      method,
      headers: {
        ...(this.credentials ? { Authorization: this.credentials } : {}),
        ...(body instanceof FormData
          ? {}
          : { "Content-Type": "application/json" }),
      },
      ...(body !== undefined
        ? { body: body instanceof FormData ? body : JSON.stringify(body) }
        : {}),
    });
    if (!response.ok) {
      let message = "Request failed (" + response.status + ")";
      try {
        const data = await response.json();
        message = data.message || data.detail || message;
      } catch {}
      throw new Error(message);
    }
    if (response.status === 204) return null;
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  }
  get(path: string) {
    return this.request(path);
  }
  post(path: string, body: unknown = {}) {
    return this.request(path, "POST", body);
  }
  put(path: string, body: unknown) {
    return this.request(path, "PUT", body);
  }
  delete(path: string) {
    return this.request(path, "DELETE");
  }
  async download(path: string, name: string) {
    const response = await fetch("/api" + path, {
      headers: { Authorization: this.credentials },
    });
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.message || data.detail ||
        (path.startsWith('/quotes/') ? 'Quotation download is available after finance approval.' : 'Download locked or unavailable.'));
    }
    const blob = await response.blob();
    if (name.endsWith('.pdf') &&
        (response.headers.get('Content-Type')?.split(';')[0] !== 'application/pdf' ||
         await blob.slice(0, 5).text() !== '%PDF-')) {
      throw new Error('The server did not return a valid PDF. Refresh the app and try again.');
    }
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = name;
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  }
  add(item: any) {
    const existing = this.cart.find((i) => i.offeringId === item.id);
    if (existing) existing.quantity++;
    else this.cart.push({ offeringId: item.id, quantity: 1, name: item.name });
  }
}
