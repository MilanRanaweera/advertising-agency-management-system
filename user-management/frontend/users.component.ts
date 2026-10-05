import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-users",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./users.component.html",
})
export class UsersComponent extends Workbench {
  config: ModuleConfig = {
    title: "People & access",
    subtitle: "Create accounts and assign one focused role per account.",
    endpoint: "users",
    art: "vivid-access-3d.webp",
    fields: [
      {key: "address", label: "Address", type: "textarea", required: false},
      {key: "contactNo", label: "Contact number", type: "tel", required: false},
      {key: "sex", label: "Sex", options: ["M", "F"], required: false},
      {key: "birthDate", label: "Date of birth", type: "date", required: false},
      { key: "name", label: "Full name", type: "text" },
      { key: "email", label: "Email", type: "email" },
      { key: "password", label: "Password (10+ characters)", type: "password" },
      {
        key: "role",
        label: "Role",
        type: "text",
        options: [
          "ADMIN",
          "SERVICE_STAFF",
          "MARKETING_MANAGER",
          "QUOTATION_STAFF",
          "FINANCE_MANAGER",
          "DESIGNER",
          "PROJECT_MANAGER",
          "FINANCE_STAFF",
          "COMMUNICATION_STAFF",
          "CUSTOMER_RELATIONS_OFFICER",
          "CUSTOMER",
        ],
      },
      { key: "active", label: "Active", type: "checkbox" },
    ],
    createRoles: ["ADMIN"],
    editRoles: ["ADMIN"],
    deleteRoles: ["ADMIN"],
    actions: [],
  };
  actionLabel(row: any, a: Action) {
    this.action(row, a);
  }
}
