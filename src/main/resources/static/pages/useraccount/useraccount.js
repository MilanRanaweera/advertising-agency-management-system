// ══════════════════════════════════════════════════════════════
// User Account Management – Frontend Logic
// Owner: Jayalath M.P.M.P.A. (IT25102962)
// ══════════════════════════════════════════════════════════════

const API_BASE = '/api/useraccount';

let allUsers = [];       // full user list from the API
let filteredUsers = [];  // currently displayed (after search/filter)
let deleteTargetId = null;

// ─── Initialisation ────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});

// ─── API Calls ─────────────────────────────────────────────────

async function loadUsers() {
    showTableSkeleton();
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error('Failed to load users');
        allUsers = await res.json();
        filteredUsers = [...allUsers];
        applyFilters();
        updateStats();
    } catch (err) {
        showToast(err.message, 'error');
        renderUsers([]);
    }
}

async function createUser(data) {
    const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) {
        const body = await res.json();
        throw new Error(body.error || Object.values(body).join(', '));
    }
    return res.json();
}

async function updateUser(id, data) {
    const res = await fetch(`${API_BASE}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) {
        const body = await res.json();
        throw new Error(body.error || Object.values(body).join(', '));
    }
    return res.json();
}

async function deleteUser(id) {
    const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete user');
}

// ─── Rendering ─────────────────────────────────────────────────

function renderUsers(users) {
    const tbody = document.getElementById('users-tbody');
    const empty = document.getElementById('empty-state');

    if (users.length === 0) {
        tbody.innerHTML = '';
        empty.style.display = 'block';
        return;
    }

    empty.style.display = 'none';
    tbody.innerHTML = users.map(u => `
        <tr>
            <td class="user-name">${escHtml(u.firstName)} ${escHtml(u.lastName)}</td>
            <td>${escHtml(u.email)}</td>
            <td>${escHtml(u.phone || '—')}</td>
            <td><span class="badge badge-${u.role.toLowerCase()}">${u.role}</span></td>
            <td><span class="badge badge-${u.status.toLowerCase()}">${u.status}</span></td>
            <td>${formatDate(u.createdAt)}</td>
            <td>
                <div class="action-btns">
                    <button class="btn btn-sm btn-edit" onclick="openEditModal(${u.id})">Edit</button>
                    <button class="btn btn-sm btn-delete" onclick="openDeleteModal(${u.id}, '${escAttr(u.firstName + ' ' + u.lastName)}')">Delete</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function showTableSkeleton() {
    const tbody = document.getElementById('users-tbody');
    const rows = Array.from({ length: 5 }, () => `
        <tr class="skeleton-row">
            <td><span class="skeleton"></span></td>
            <td><span class="skeleton"></span></td>
            <td><span class="skeleton" style="width:60%"></span></td>
            <td><span class="skeleton" style="width:50%"></span></td>
            <td><span class="skeleton" style="width:50%"></span></td>
            <td><span class="skeleton" style="width:70%"></span></td>
            <td><span class="skeleton" style="width:40%"></span></td>
        </tr>
    `).join('');
    tbody.innerHTML = rows;
    document.getElementById('empty-state').style.display = 'none';
}

function updateStats() {
    document.getElementById('stat-total').textContent = allUsers.length;
    document.getElementById('stat-active').textContent =
        allUsers.filter(u => u.status === 'ACTIVE').length;
    document.getElementById('stat-inactive').textContent =
        allUsers.filter(u => u.status === 'INACTIVE').length;
    document.getElementById('stat-admin').textContent =
        allUsers.filter(u => u.role === 'ADMIN').length;
}

// ─── Search & Filter ───────────────────────────────────────────

let searchTimeout;

function handleSearch() {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(applyFilters, 300);   // 300ms debounce
}

function handleFilter() {
    applyFilters();
}

function applyFilters() {
    const keyword = document.getElementById('search-input').value.trim().toLowerCase();
    const role    = document.getElementById('filter-role').value;
    const status  = document.getElementById('filter-status').value;

    filteredUsers = allUsers.filter(u => {
        const matchKeyword = !keyword ||
            u.firstName.toLowerCase().includes(keyword) ||
            u.lastName.toLowerCase().includes(keyword) ||
            u.email.toLowerCase().includes(keyword);
        const matchRole   = !role   || u.role === role;
        const matchStatus = !status || u.status === status;
        return matchKeyword && matchRole && matchStatus;
    });

    renderUsers(filteredUsers);
}

// ─── Modal – Add / Edit ────────────────────────────────────────

function openAddModal() {
    document.getElementById('modal-title').textContent = 'Add New User';
    document.getElementById('btn-submit-text').textContent = 'Create User';
    document.getElementById('form-user-id').value = '';
    document.getElementById('user-form').reset();
    document.getElementById('form-password').required = true;
    document.getElementById('pwd-required').style.display = 'inline';
    document.getElementById('pwd-hint').textContent = '';
    document.getElementById('modal-overlay').classList.add('active');
}

function openEditModal(id) {
    const user = allUsers.find(u => u.id === id);
    if (!user) return;

    document.getElementById('modal-title').textContent = 'Edit User';
    document.getElementById('btn-submit-text').textContent = 'Update User';
    document.getElementById('form-user-id').value = user.id;
    document.getElementById('form-firstName').value = user.firstName;
    document.getElementById('form-lastName').value = user.lastName;
    document.getElementById('form-email').value = user.email;
    document.getElementById('form-phone').value = user.phone || '';
    document.getElementById('form-role').value = user.role;
    document.getElementById('form-status').value = user.status;
    document.getElementById('form-password').value = '';
    document.getElementById('form-password').required = false;
    document.getElementById('pwd-required').style.display = 'none';
    document.getElementById('pwd-hint').textContent = 'Leave blank to keep current password';
    document.getElementById('modal-overlay').classList.add('active');
}

function closeModal() {
    document.getElementById('modal-overlay').classList.remove('active');
}

async function handleFormSubmit(event) {
    event.preventDefault();
    const submitBtn = document.getElementById('btn-submit');
    submitBtn.disabled = true;

    const userId = document.getElementById('form-user-id').value;
    const data = {
        firstName: document.getElementById('form-firstName').value.trim(),
        lastName:  document.getElementById('form-lastName').value.trim(),
        email:     document.getElementById('form-email').value.trim(),
        phone:     document.getElementById('form-phone').value.trim(),
        role:      document.getElementById('form-role').value,
        status:    document.getElementById('form-status').value,
        password:  document.getElementById('form-password').value
    };

    try {
        if (userId) {
            // Update – if password is empty, send a dummy to pass validation
            // (backend will only hash if non-blank)
            if (!data.password) data.password = '______';  // placeholder, service ignores blanks
            await updateUser(userId, data);
            showToast('User updated successfully', 'success');
        } else {
            await createUser(data);
            showToast('User created successfully', 'success');
        }
        closeModal();
        loadUsers();
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        submitBtn.disabled = false;
    }
}

// ─── Modal – Delete ────────────────────────────────────────────

function openDeleteModal(id, name) {
    deleteTargetId = id;
    document.getElementById('delete-user-name').textContent = name;
    document.getElementById('delete-overlay').classList.add('active');
}

function closeDeleteModal() {
    document.getElementById('delete-overlay').classList.remove('active');
    deleteTargetId = null;
}

async function confirmDelete() {
    if (!deleteTargetId) return;
    const btn = document.getElementById('btn-confirm-delete');
    btn.disabled = true;
    try {
        await deleteUser(deleteTargetId);
        showToast('User deleted successfully', 'success');
        closeDeleteModal();
        loadUsers();
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
    }
}

// ─── Toast ─────────────────────────────────────────────────────

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    document.getElementById('toast-message').textContent = message;
    toast.className = 'toast show toast-' + type;
    setTimeout(() => { toast.className = 'toast'; }, 3500);
}

// ─── Helpers ───────────────────────────────────────────────────

function escHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function escAttr(str) {
    return str.replace(/'/g, "\\'").replace(/"/g, '&quot;');
}

function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleDateString('en-GB', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
}
