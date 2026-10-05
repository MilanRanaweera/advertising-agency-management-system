import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-payments",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./payments.component.html",
})
export class PaymentsComponent extends Workbench {
  config: ModuleConfig = {
    title: "Payments & invoices",
    subtitle: "Review billing, generate and send invoices, and track payments and receipts.",
    endpoint: "invoices",
    art: "vivid-payments-3d.webp",
    fields: [
      { key: "projectId", label: "Project ID", type: "number" },
      {
        key: "billingName",
        label: "Billing name",
        type: "text",
        required: true,
      },
      {
        key: "billingAddress",
        label: "Billing address",
        type: "textarea",
        required: true,
      },
    ],
    createRoles: ["FINANCE_STAFF"],
    editRoles: ["FINANCE_STAFF"],
    deleteRoles: ["FINANCE_STAFF", "FINANCE_MANAGER"],
    actions: [
      {label: "Approve for invoice", name: "approve", roles: ["FINANCE_STAFF"], states: ["DRAFT", "UNPAID", "CHANGES"]},
      {label: "Generate invoice", name: "generate", roles: ["FINANCE_MANAGER"], states: ["REVIEW"]},
      {label: "Return for corrections", name: "return", roles: ["FINANCE_MANAGER"], states: ["REVIEW", "GENERATED"]},
      {label: "Send invoice", name: "send", roles: ["FINANCE_MANAGER"], states: ["GENERATED"]},
      {label: "Pay invoice", name: "pay", roles: ["CUSTOMER"], states: ["SENT"]},
    ],
  };
  receipt(row: any) {
    void this.attempt(() => this.api.download('/invoices/' + row.id + '/receipt', 'receipt-' + row.id + '.pdf'));
  }
  actionLabel(row: any, a: Action) { this.action(row, a); }
}
