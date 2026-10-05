import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-quotes",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./quotes.component.html",
})
export class QuotesComponent extends Workbench {
  config: ModuleConfig = {
    title: "Quotation desk",
    subtitle: "Brief → generation → finance review → customer acceptance.",
    endpoint: "quotes",
    art: "vivid-quotations-3d.webp",
    fields: [
      { key: "title", label: "Project title", type: "text" },
      {
        key: "requirements",
        label: "Requirements and additional requests",
        type: "textarea",
      },
      {key: 'additionalAmount', label: 'Additional price (LKR)', type: 'number', required: false},
      {key: 'additionalDescription', label: 'Reason for additional price', type: 'textarea', required: false},
    ],
    createRoles: ["CUSTOMER"],
    editRoles: ["QUOTATION_STAFF"],
    deleteRoles: ["CUSTOMER", "QUOTATION_STAFF"],
    actions: [
      {
        label: "Submit for finance approval",
        name: "generate",
        roles: ["QUOTATION_STAFF"],
        states: ["REQUESTED", "CHANGES"],
      },
      {
        label: "Approve",
        name: "approve",
        roles: ["FINANCE_MANAGER"],
        states: ["REVIEW"],
      },
      {
        label: "Return with feedback",
        name: "return",
        roles: ["FINANCE_MANAGER"],
        states: ["REVIEW"],
      },
      {
        label: "Send quotation",
        name: "send",
        roles: ["FINANCE_MANAGER"],
        states: ["APPROVED", "SENT", "ACCEPTED"],
      },
      {
        label: "Accept quotation & start project",
        name: "accept",
        roles: ["CUSTOMER"],
        states: ["APPROVED", "SENT"],
      },
    ],
  };
  actionLabel(row: any, a: Action) {
    this.action(row, a);
  }
}
