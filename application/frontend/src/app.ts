import { Component, OnInit, AfterViewInit, inject, ChangeDetectorRef } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from "@angular/router";
import { Api } from "./api";
import { roleLabel } from './roles';
export const sections = [
  { path: "users", name: "Users & access", roles: ["ADMIN"] },
  {
    path: "catalog",
    name: "Services & packages",
    roles: ["SERVICE_STAFF", "MARKETING_MANAGER", "CUSTOMER"],
  },
  {
    path: "quotes",
    name: "Quotations",
    roles: ["QUOTATION_STAFF", "FINANCE_MANAGER", "PROJECT_MANAGER", "CUSTOMER"],
  },
  {
    path: "projects",
    name: "Projects",
    roles: [
      "PROJECT_MANAGER",
      "DESIGNER",
      "FINANCE_STAFF",
      "FINANCE_MANAGER",
      "COMMUNICATION_STAFF",
      "CUSTOMER_RELATIONS_OFFICER",
      "CUSTOMER",
    ],
  },
  {
    path: "invoices",
    name: "Payments & invoices",
    roles: ["FINANCE_STAFF", "FINANCE_MANAGER", "CUSTOMER"],
  },
  {
    path: "communications",
    name: "Communications",
    roles: ["COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER", "CUSTOMER"],
  },
];
@Component({
  selector: "app-root",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
  ],
  templateUrl: "./app.html",
})
export class AppComponent implements OnInit, AfterViewInit {
  readonly currentYear = new Date().getFullYear();
  roleLabel = roleLabel;
  private changes = inject(ChangeDetectorRef);
  api = inject(Api);
  router = inject(Router);
  offerings: any[] = [];
  error = "";
  authOpen = false;
  register = false;
  name = "";
  address = "";
  contactNo = "";
  sex = "";
  birthDate = "";
  today = new Date().toISOString().slice(0, 10);
  email = "";
  password = "";
  busy = false;
  get navigation() {
    return sections.filter((s) => s.roles.includes(this.api.user?.role || ""));
  }
  async ngOnInit() {
    try {
      this.offerings = await this.api.get("/catalog/public");
    } catch {
      this.error = "Start the backend to load services and packages.";
    } finally {
      this.changes.markForCheck();
    }
  }
  ngAfterViewInit() {
    setTimeout(() => this.globe(), 100);
  }
  globe() {
    (window as any).AxiomHero3D?.mount();
  }
  openAuth(register = false) {
    this.register = register;
    this.authOpen = true;
    this.error = "";
  }
  async authenticate() {
    if (this.busy) return;
    this.busy = true;
    this.error = "";
    try {
      if (this.register)
        await this.api.post("/auth/register", {
          name: this.name,
          address: this.address, contactNo: this.contactNo, sex: this.sex, birthDate: this.birthDate,
          email: this.email,
          password: this.password,
        });
      await this.api.login(this.email, this.password);
      this.password = "";
      this.authOpen = false;
      (window as any).AxiomHero3D?.dispose();
      const defaultPath: Record<string, string> = {
        ADMIN: "users",
        SERVICE_STAFF: "catalog",
        MARKETING_MANAGER: "catalog",
        QUOTATION_STAFF: "quotes",
        FINANCE_MANAGER: "quotes",
        DESIGNER: "projects",
        PROJECT_MANAGER: "projects",
        FINANCE_STAFF: "invoices",

        COMMUNICATION_STAFF: "communications",
        CUSTOMER_RELATIONS_OFFICER: "communications",
        CUSTOMER: "projects",
      };
      await this.router.navigateByUrl("/" + defaultPath[this.api.user!.role]);
    } catch (e: any) {
      this.error = e.message;
    } finally {
      this.busy = false;
      this.changes.markForCheck();
    }
  }
  logout() {
    this.api.logout();
    void this.router.navigateByUrl("/");
    setTimeout(() => this.globe(), 100);
  }
  artwork(item: any) {
    const n = item.name.toLowerCase();
    const type =
      item.kind === "Package"
        ? n.includes("growth")
          ? "marketing"
          : n.includes("signature")
            ? "projects"
            : "packages"
        : n.includes("social")
          ? "marketing"
          : n.includes("digital")
            ? "projects"
            : "services";
    return "/assets/vivid-" + type + "-3d.webp";
  }
}
