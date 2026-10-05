import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Workbench,
  ModuleConfig,
  Action,
} from "../../application/frontend/src/workbench";
@Component({
  selector: "axiom-projects",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./projects.component.html",
})
export class ProjectsComponent extends Workbench {
  config: ModuleConfig = {
    title: "Projects in motion",
    subtitle: "Assign designers, review prototypes, and follow progress.",
    endpoint: "projects",
    art: "vivid-projects-3d.webp",
    fields: [
      { key: "quotationId", label: "Accepted quotation ID", type: "number" },
    ],
    createRoles: ["PROJECT_MANAGER"],
    editRoles: [],
    deleteRoles: ["PROJECT_MANAGER"],
    actions: [{label: "Confirm project complete", name: "complete", roles: ["PROJECT_MANAGER"], states: ["FINAL_REVIEW"]}],
  };
  actionLabel(row: any, a: Action) {
    this.action(row, a);
  }
}
