async function api(path, method = "GET", body) {
    const headers = { "Content-Type": "application/json" };
    const userId = localStorage.getItem("userId");
    if (userId) headers["X-User-Id"] = userId;

    const res = await fetch(path, {
        method, headers,
        body: body ? JSON.stringify(body) : undefined
    });
    const json = await res.json();
    if (!res.ok || json.success === false) throw new Error(json.message || "Request failed");
    return json.data;
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const data = await api("/api/auth/login", "POST", {
            email: document.getElementById("loginEmail").value,
            password: document.getElementById("loginPassword").value
        });
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("role", data.role);
        localStorage.setItem("fullName", data.fullName);
        renderUser();
        alert("Welcome " + data.fullName);
    } catch (err) { alert(err.message); }
});

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const data = await api("/api/auth/register", "POST", {
            firstName: document.getElementById("regFirst").value,
            lastName:  document.getElementById("regLast").value,
            email:     document.getElementById("regEmail").value,
            phone:     document.getElementById("regPhone").value,
            password:  document.getElementById("regPassword").value
        });
        localStorage.setItem("userId", data.userId);
        localStorage.setItem("role", data.role);
        localStorage.setItem("fullName", data.fullName);
        renderUser();
        alert("Registered successfully");
    } catch (err) { alert(err.message); }
});

async function loadServices() {
    try {
        const list = await api("/api/services");
        document.getElementById("servicesList").innerHTML =
            "<h3>Services</h3><ul>" + list.map(s =>
                `<li>#${s.serviceId} – ${s.serviceName} (${s.category ?? '-'}) – LKR ${s.basePrice}</li>`
            ).join("") + "</ul>";
    } catch (e) { alert(e.message); }
}

async function loadPackages() {
    try {
        const list = await api("/api/packages");
        document.getElementById("packagesList").innerHTML =
            "<h3>Packages</h3><ul>" + list.map(p =>
                `<li>#${p.packageId} – ${p.packageName} – LKR ${p.price} – ${p.status}</li>`
            ).join("") + "</ul>";
    } catch (e) { alert(e.message); }
}

async function loadMyProfile() {
    try {
        const user = await api("/api/users/me");
        document.getElementById("profileBox").innerHTML =
            `<h3>Profile</h3><pre>${JSON.stringify(user, null, 2)}</pre>`;
    } catch (e) { alert(e.message); }
}

function renderUser() {
    const name = localStorage.getItem("fullName");
    const role = localStorage.getItem("role");
    document.getElementById("userInfo").textContent = name ? `${name} (${role})` : "";
    const show = name ? "none" : "grid";
    document.getElementById("loginForm").style.display = show;
    document.getElementById("registerForm").style.display = show;
}

renderUser();