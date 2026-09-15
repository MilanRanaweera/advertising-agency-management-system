/* dash-shared.js */
const API = '';

function getUser() {
  const u = JSON.parse(localStorage.getItem('aams_user') || 'null');
  if (!u) { window.location.href = '/'; return null; }
  return u;
}

function logout() {
  localStorage.removeItem('aams_user');
  window.location.href = '/';
}

function initUser() {
  const u = getUser();
  if (!u) return;
  const nameEl = document.getElementById('userName');
  const roleEl = document.getElementById('userRole');
  const initEl = document.getElementById('userInitial');
  if (nameEl) nameEl.textContent = u.fullName || u.name || u.email;
  if (roleEl) roleEl.textContent = roleLabel(u.role);
  if (initEl) initEl.textContent = (u.fullName || u.email || 'U')[0].toUpperCase();
}

function roleLabel(role) {
  const m = {
    CUSTOMER:'Customer', ADMIN:'Administrator', MANAGER:'Managing Director',
    DESIGNER:'Designer', MKT_MANAGER:'Marketing Manager',
    SALES_REP:'Sales Representative', OFFICER:'Customer Relation Officer'
  };
  return m[role] || role;
}

async function apiFetch(path, opts = {}) {
  const res = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json', ...opts.headers },
    ...opts
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Request failed');
  return data.data ?? data;
}

function formatDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-GB', { day:'2-digit', month:'short', year:'numeric' });
}

function statusPill(status) {
  const map = {
    ACTIVE:'green', PENDING:'orange', PAID:'green', UNPAID:'red',
    IN_PROGRESS:'blue', COMPLETED:'green', CANCELLED:'red',
    OPEN:'orange', RESOLVED:'green', SCHEDULED:'purple', APPROVED:'green'
  };
  return `<span class="pill ${map[status]||'purple'}">${status}</span>`;
}

document.addEventListener('DOMContentLoaded', initUser);