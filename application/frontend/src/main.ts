import { bootstrapApplication } from "@angular/platform-browser";
import { provideRouter, Routes, CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";
import { AppComponent, sections } from "./app";
import { Api } from "./api";
import { UsersComponent } from "../../../user-management/frontend/users.component";
import { CatalogComponent } from "../../../service-package-management/frontend/catalog.component";
import { QuotesComponent } from "../../../quotation-management/frontend/quotes.component";
import { ProjectsComponent } from "../../../project-management/frontend/projects.component";
import { PaymentsComponent } from "../../../payment-invoice-management/frontend/payments.component";
import { CommunicationsComponent } from "../../../communication-management/frontend/communications.component";
const guard: CanActivateFn = (route) => {
  const api = inject(Api);
  return api.user && route.data["roles"].includes(api.user.role)
    ? true
    : inject(Router).createUrlTree(["/"]);
};
const components = [
  UsersComponent,
  CatalogComponent,
  QuotesComponent,
  ProjectsComponent,
  PaymentsComponent,
  CommunicationsComponent,
];
const routes: Routes = sections.map((s, i) => ({
  path: s.path,
  component: components[i],
  canActivate: [guard],
  data: { roles: s.roles },
}));
routes.push(
  { path: "", pathMatch: "full", children: [] },
  { path: "**", redirectTo: "" },
);
bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes)],
}).catch(console.error);
