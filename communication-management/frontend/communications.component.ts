import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-communications",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./communications.component.html",
})
export class CommunicationsComponent extends Workbench {
  config: ModuleConfig = {
    title: "Communication hub",
    subtitle: "Customer conversations and consultation appointments.",
    endpoint: "communications",
    art: "vivid-communications-3d.webp",
    fields: [
      { key: "subject", label: "Subject", type: "text" },
      {
        key: "kind",
        label: "Type",
        type: "text",
        options: ["MESSAGE", "CONSULTATION"],
      },
      { key: "message", label: "Message", type: "textarea" },
      {
        key: "appointmentAt",
        label: "Consultation time (Sri Lanka)",
        type: "datetime-local",
        required: false,
      },
    ],
    createRoles: ["CUSTOMER", "COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER"],
    editRoles: ["CUSTOMER", "COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER"],
    deleteRoles: ["CUSTOMER", "CUSTOMER_RELATIONS_OFFICER"],
    actions: [
      {
        label: "Reply",
        name: "reply",
        roles: ["COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER", "CUSTOMER"],
        states: ["OPEN", "CONFIRMED"],
      },
      {
        label: "Confirm appointment",
        name: "confirm",
        roles: ["CUSTOMER_RELATIONS_OFFICER"],
        states: ["OPEN"],
      },
      {
        label: "Mark as solved",
        name: "solve",
        roles: ["COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER"],
        states: ["OPEN", "CONFIRMED"],
      },
    ],
  };
  override actions(row: any) {
    return super.actions(row).filter(a =>
      (a.name !== 'confirm' || row.kind === 'CONSULTATION') &&
      (a.name !== 'reply' || this.role !== 'CUSTOMER' || row.reply ||
        row.replies?.some((r: any) => r.authorRole !== 'CUSTOMER')));
  }
  actionLabel(row: any, a: Action) {
    this.action(row, a);
  }
}
