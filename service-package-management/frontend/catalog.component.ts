import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-catalog",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./catalog.component.html",
})
export class CatalogComponent extends Workbench {
  config: ModuleConfig = {
    title: "Services & packages",
    subtitle: "Create offerings, review them, and publish approved work.",
    endpoint: "catalog",
    art: "vivid-services-3d.webp",
    fields: [
      { key: "name", label: "Name", type: "text" },
      {
        key: "kind",
        label: "Type",
        type: "text",
        options: ["Service", "Package", "Promotion"],
      },
      { key: "description", label: "Description", type: "textarea" },
      { key: "deliverables", label: "Included deliverables", type: "textarea" },
      {
        key: "price",
        label: "Price / promotion offer price (LKR)",
        type: "number",
      },
    ],
    createRoles: ["SERVICE_STAFF"],
    editRoles: ["SERVICE_STAFF"],
    deleteRoles: ["SERVICE_STAFF", "MARKETING_MANAGER"],
    actions: [
      {
        label: "Submit for review",
        name: "submit",
        roles: ["SERVICE_STAFF"],
        states: ["DRAFT", "CHANGES"],
      },
      {
        label: "Approve & publish",
        name: "approve",
        roles: ["MARKETING_MANAGER"],
        states: ["REVIEW"],
      },
      {
        label: "Request changes",
        name: "return",
        roles: ["MARKETING_MANAGER"],
        states: ["REVIEW", "PUBLISHED"],
      },
    ],
  };
  actionLabel(row: any, a: Action) {
    this.action(row, a);
  }
}
