import { Directive, inject, OnInit, OnDestroy, ChangeDetectorRef } from "@angular/core";
import { Api } from "./api";
import { roleLabel } from './roles';
import { Router } from '@angular/router';
export interface Field {
  key: string;
  label: string;
  type?: string;
  options?: string[];
  required?: boolean;
}
export interface Action {
  label: string;
  name: string;
  roles: string[];
  states: string[];
}
export interface ModuleConfig {
  title: string;
  subtitle: string;
  endpoint: string;
  art: string;
  fields: Field[];
  createRoles: string[];
  editRoles: string[];
  deleteRoles: string[];
  actions: Action[];
}
@Directive()
export abstract class Workbench implements OnInit, OnDestroy {
  private changes = inject(ChangeDetectorRef);
  private refreshTimer?: ReturnType<typeof setInterval>;
  private refreshing = false;
  private loadVersion = 0;
  api = inject(Api);
  router = inject(Router);
  roleLabel = roleLabel;
  startedQuoteIds: number[] = [];
  abstract config: ModuleConfig;
  rows: any[] = [];
  loading = false;
  busy = false;
  error = "";
  notice = "";
  modal = "";
  editing: any = null;
  form: any = {};
  selected: any = null;
  assets: any[] = [];
  feedback: any[] = [];
  team: any[] = [];
  designers: any[] = [];
  outbox: any[] = [];
  projects: any[] = [];
  invoices: any[] = [];
  search = "";
  get role() {
    return this.api.user?.role || "";
  }
  get shown() {
    return this.rows.filter((r) =>
      JSON.stringify(r).toLowerCase().includes(this.search.toLowerCase()),
    );
  }
  get pending() {
    return this.rows.filter((r) =>
      [
        "REVIEW",
        "REQUESTED",
        "OPEN",
        "CLIENT_REVIEW",
        "DIRECTOR_REVIEW",
        "FINAL_REVIEW",
        "DRAFT",
      ].includes(r.status),
    ).length;
  }
  ngOnInit() {
    void this.load();
    this.refreshTimer = setInterval(() => {
      if (!this.busy && !this.refreshing && !this.modal && !document.hidden) void this.load(true);
    }, 15000);
  }
  ngOnDestroy() {
    clearInterval(this.refreshTimer);
  }
  async attempt(fn: () => Promise<void>) {
    if (this.busy) return;
    this.busy = true;
    this.error = "";
    try {
      await fn();
    } catch (e: any) {
      this.error = e.message || "Unable to complete request";
    } finally {
      this.busy = false;
      this.changes.markForCheck();
    }
  }
  trackRow(_index: number, row: any) {
    return row.id;
  }
  async load(background = false, force = false) {
    if (background && this.refreshing && !force) return;
    const version = ++this.loadVersion;
    this.refreshing = true;
    if (!background) this.loading = true;
    try {
      const rows = await this.api.get(
        this.config.endpoint === "catalog" && this.role === "CUSTOMER"
          ? "/catalog/public"
          : "/" + this.config.endpoint,
      );
      if (version !== this.loadVersion) return;
      this.rows = rows;
      if (this.config.endpoint === "projects" && this.role === "CUSTOMER")
        this.invoices = await this.api.get("/invoices");
      if (this.config.endpoint === 'quotes' && this.role === 'PROJECT_MANAGER') {
        const projects = await this.api.get('/projects');
        this.startedQuoteIds = projects.map((p: any) => p.quotationId);
      }
    } catch (e: any) {
      if (version === this.loadVersion) this.error = e.message;
    } finally {
      if (version === this.loadVersion) {
        this.refreshing = false;
        this.loading = false;
        this.changes.markForCheck();
      }
    }
  }
  can(roles: string[]) {
    return roles.includes(this.role);
  }
  actions(row: any) {
    return this.config.actions.filter(
      (a) => this.can(a.roles) && a.states.includes(row.status) && (a.name !== "complete" || row.progress === 100),
    );
  }
  editable(row: any) {
    return (
      this.can(this.config.editRoles) &&
      (this.config.endpoint === "users" ||
        ["DRAFT", "CHANGES", "REQUESTED", "UNPAID", "OPEN"].includes(
          row.status,
        ))
    );
  }
  removable(row: any) {
    if (this.config.endpoint === "invoices" && this.role === "FINANCE_MANAGER") return true;
    if (this.config.endpoint === 'catalog' && this.role === 'MARKETING_MANAGER') return true;
    if (this.config.endpoint === 'users' && row.id === this.api.user?.id) return false;
    return (
      this.can(this.config.deleteRoles) &&
      (this.config.endpoint === "users" ||
        ["DRAFT", "CHANGES", "REQUESTED", "PLANNED", "UNPAID", "OPEN"].includes(
          row.status,
        ))
    );
  }
  create() {
    this.form = {
      kind: this.config.endpoint === "catalog" ? "Service" : "MESSAGE",
      role: "CUSTOMER",
      active: true,
      capacity: 3,
    };
    this.editing = null;
    this.modal = "edit";
  }
  edit(row: any) {
    this.editing = row;
    this.form = { ...row };
    this.modal = "edit";
  }
  get fields() {
    if (this.editing && this.config.endpoint === "quotes")
      return this.config.fields.filter((f) => f.key !== "title");
    if (!this.editing && this.config.endpoint === "quotes")
      return this.config.fields.filter((f) => !f.key.startsWith('additional'));
    if (this.editing && this.config.endpoint === "invoices")
      return this.config.fields.filter((f) => f.key !== "projectId");
    if (this.editing && this.config.endpoint === "users")
      return this.config.fields.filter(
        (f) => !["email", "password"].includes(f.key),
      );
    return this.config.fields;
  }
  save() {
    void this.attempt(async () => {
      let body = { ...this.form };
      if (this.config.endpoint === "quotes" && !this.editing) {
        body.items = this.api.cart.map((i) => ({
          offeringId: i.offeringId,
          quantity: i.quantity,
        }));
        if (!body.items.length)
          throw new Error("Add services or packages to your brief first.");
      }
      if (this.editing)
        await this.api.put(
          "/" + this.config.endpoint + "/" + this.editing.id,
          body,
        );
      else await this.api.post("/" + this.config.endpoint, body);
      if (this.config.endpoint === "quotes" && !this.editing)
        this.api.cart = [];
      this.modal = "";
      this.notice = "Saved successfully";
      await this.load(true, true);
    });
  }
  confirmDelete(row: any) {
    this.selected = row;
    this.modal = "delete";
  }
  remove() {
    void this.attempt(async () => {
      await this.api.delete(
        "/" + this.config.endpoint + "/" + this.selected.id,
      );
      this.modal = "";
      this.notice =
        this.config.endpoint === "users"
          ? "Account deleted from the database"
          : "Record deleted";
      await this.load();
    });
  }
  action(row: any, a: Action) {
    this.selected = row;
    this.form = { action: a.name, actionLabel: a.label, feedback: "", reply: "" };
    this.modal = "action";
  }
  runAction() {
    void this.attempt(async () => {
      await this.api.post(
        "/" +
          this.config.endpoint +
          "/" +
          this.selected.id +
          "/" +
          this.form.action,
        this.form,
      );
      this.modal = "";
      this.notice = "Action completed";
      await this.load();
    });
  }
  document(row: any) {
    void this.attempt(() =>
      this.api.download(
        "/" + this.config.endpoint + "/" + row.id + "/document",
        this.config.endpoint + "-" + row.id + (['quotes', 'invoices'].includes(this.config.endpoint) ? '.pdf' : '.html'),
      ),
    );
  }
  async details(row: any) {
    this.selected = row;
    await this.attempt(async () => {
      [this.assets, this.feedback, this.team] = await Promise.all([
        this.api.get("/projects/" + row.id + "/assets"),
        this.api.get("/projects/" + row.id + "/feedback"),
        this.api.get("/projects/" + row.id + "/team"),
      ]);
      if (this.role === "PROJECT_MANAGER") {
        this.designers = await this.api.get("/users/designers");
        const all = await this.api.get("/projects");
        const teams = await Promise.all(
          all
            .filter((p: any) => p.status !== "COMPLETED")
            .map((p: any) => this.api.get("/projects/" + p.id + "/team")),
        );
        this.designers = this.designers.map((d) => ({
          ...d,
          load: teams.flat().filter((a: any) => a.designerId === d.id).length,
        }));
      }
      this.form = {
        progress: row.progress,
        approved: true,
        message: "",
        kind: "PROTOTYPE",
        designers: this.team.map((t) => t.designerId),
      };
      this.modal = "project";
    });
  }
  toggleDesigner(id: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.form.designers = checked
      ? [...this.form.designers, id]
      : this.form.designers.filter((x: number) => x !== id);
  }
  projectAction(action: string) {
    void this.attempt(async () => {
      const base = "/projects/" + this.selected.id;
      if (action === "review") await this.api.post(base + "/review", this.form);
      else await this.api.put(base + "/" + action, this.form);
      this.modal = "";
      await this.load();
    });
  }
  upload(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    void this.attempt(async () => {
      const data = new FormData();
      data.append("file", file);
      data.append("kind", this.form.kind);
      await this.api.post("/projects/" + this.selected.id + "/assets", data);
      this.modal = "";
      await this.load();
    });
  }
  originalUnlocked(asset: any) {
    return (
      asset.kind !== "ORIGINAL" ||
      this.role !== "CUSTOMER" ||
      this.invoices.some(
        (i) => i.projectId === asset.projectId && i.status === "PAID",
      )
    );
  }
  downloadAsset(asset: any) {
    void this.attempt(() =>
      this.api.download(
        "/projects/assets/" + asset.id + "/download",
        asset.originalName,
      ),
    );
  }
  viewOutbox() {
    void this.attempt(async () => {
      this.outbox = await this.api.get("/communications/outbox");
      this.modal = "outbox";
    });
  }
  add(row: any) {
    this.api.add(row);
    this.notice = row.name + " added to your brief";
  }
  canDownloadQuote(row: any) {
    return ['APPROVED', 'SENT', 'ACCEPTED'].includes(row.status) &&
      ['CUSTOMER', 'QUOTATION_STAFF', 'FINANCE_MANAGER'].includes(this.role);
  }
  changeQuantity(item: any, delta: number) {
    item.quantity = Math.min(100, item.quantity + delta);
    if (item.quantity <= 0) this.removeCartItem(item);
  }
  removeCartItem(item: any) {
    this.api.cart = this.api.cart.filter(i => i.offeringId !== item.offeringId);
  }
  startProject(row: any) {
    void this.attempt(async () => {
      await this.api.post('/projects', {quotationId: row.id});
      await this.router.navigateByUrl('/projects');
    });
  }
  saveCapacity(designer: any) {
    void this.attempt(async () => {
      await this.api.put('/users/designers/' + designer.id + '/capacity', {capacity: designer.capacity});
      this.notice = 'Designer capacity updated';
    });
  }
}
