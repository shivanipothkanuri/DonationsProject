// ── Token helpers ────────────────────────────────────────────
const TOKEN_KEY = 'cams_token';
const USER_KEY  = 'cams_user';

function saveAuth(token, user) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}
function getToken()       { return localStorage.getItem(TOKEN_KEY); }
function getUser()        { const u = localStorage.getItem(USER_KEY); return u ? JSON.parse(u) : null; }
function getCurrentUser() { return getUser(); }
function getRole()        { const u = getUser(); return u ? u.role : null; }
function isLoggedIn() { return !!getToken(); }

function logout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  window.location.href = '/index.html';
}

// Redirect to login if not authenticated
function requireAuth() {
  if (!isLoggedIn()) { window.location.href = '/index.html'; }
}

// Redirect to dashboard if already logged in (for login page)
function redirectIfLoggedIn() {
  if (isLoggedIn()) { window.location.href = '/dashboard.html'; }
}

// ── Fetch wrapper ────────────────────────────────────────────
// Supports two call styles:
//   Old: api('POST', '/auth/login', bodyObj)  → returns parsed JSON; throws on error
//   New: api('/api/users', { method, body })  → returns Response; caller checks res.ok
const _HTTP_METHODS = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'];
async function api(urlOrMethod, pathOrOpts = {}, legacyBody = null) {
  const isOldStyle = _HTTP_METHODS.includes(String(urlOrMethod).toUpperCase());

  let url, method, bodyStr;
  if (isOldStyle) {
    method  = urlOrMethod.toUpperCase();
    url     = '/api' + pathOrOpts;            // pathOrOpts is the path string
    bodyStr = legacyBody ? JSON.stringify(legacyBody) : null;
  } else {
    url     = urlOrMethod;
    const opts = (typeof pathOrOpts === 'object' && pathOrOpts !== null) ? pathOrOpts : {};
    method  = (opts.method || 'GET').toUpperCase();
    bodyStr = opts.body || null;
  }

  const options = { method, headers: { 'Content-Type': 'application/json' } };
  const token = getToken();
  const hasAuthToken = !!token;
  if (token) options.headers['Authorization'] = 'Bearer ' + token;
  if (bodyStr) options.body = bodyStr;

  const res = await fetch(url, options);
  if (res.status === 401 && hasAuthToken) {
    logout();
    throw new Error('Session expired');
  }

  if (isOldStyle) {
    // Old style: parse and return JSON directly; throw on non-ok
    const json = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(json.message || 'Request failed');
    return json;
  }
  // New style: return Response so caller can do res.ok / res.json()
  return res;
}

// ── Shared nav builder ───────────────────────────────────────
// Pass the element ID of the <nav> to populate (e.g. 'mainNav').
// Active link is auto-detected from window.location.pathname.
function buildNav(navElementId) {
  const navEl = document.getElementById(navElementId);
  const user = getUser();
  const role = getRole();
  const roleLabel = role ? role.replace('_', ' ') : '';
  const activePage = window.location.pathname;

  const links = [
    { href: '/dashboard.html',     label: 'Dashboard',     roles: ['DONOR','STAFF','CASE_MANAGER','ADMINISTRATOR','LEADERSHIP'] },
    { href: '/campaigns.html',     label: 'Campaigns',     roles: ['DONOR','STAFF','CASE_MANAGER','ADMINISTRATOR','LEADERSHIP'] },
    { href: '/donations.html',     label: 'My Donations',  roles: ['DONOR'] },
    { href: '/beneficiaries.html', label: 'Beneficiaries', roles: ['STAFF','CASE_MANAGER','ADMINISTRATOR'] },
    { href: '/aid-requests.html',  label: 'Aid Requests',  roles: ['STAFF','CASE_MANAGER','ADMINISTRATOR'] },
    { href: '/inventory.html',     label: 'Inventory',     roles: ['STAFF','ADMINISTRATOR'] },
    { href: '/reports.html',       label: 'Reports',       roles: ['STAFF','CASE_MANAGER','ADMINISTRATOR','LEADERSHIP'] },
    { href: '/users.html',         label: 'Users',         roles: ['ADMINISTRATOR'] },
  ];

  const navLinks = links
    .filter(l => l.roles.includes(role))
    .map(l => `<li class="nav-item">
      <a class="nav-link${l.href === activePage ? ' active fw-bold' : ''}" href="${l.href}">${l.label}</a>
    </li>`)
    .join('');

  const innerHtml = `
    <div class="container-fluid">
      <a class="navbar-brand fw-bold" href="/dashboard.html">🤝 CharityAid</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navMenu">
        <ul class="navbar-nav me-auto">${navLinks}</ul>
        <ul class="navbar-nav">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
              👤 ${user ? user.fullName : ''} <span class="badge bg-light text-success ms-1">${roleLabel}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="/profile.html">My Profile</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item text-danger" href="#" onclick="logout()">Logout</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>`;

  if (navEl) {
    navEl.className = 'navbar navbar-expand-lg navbar-dark bg-success shadow-sm';
    navEl.innerHTML = innerHtml;
  }
}

// ── Toast notifications ──────────────────────────────────────
function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'position-fixed bottom-0 end-0 p-3';
    container.style.zIndex = 9999;
    document.body.appendChild(container);
  }
  const id = 'toast-' + Date.now();
  container.insertAdjacentHTML('beforeend', `
    <div id="${id}" class="toast align-items-center text-white bg-${type} border-0" role="alert">
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    </div>`);
  const el = document.getElementById(id);
  const toast = new bootstrap.Toast(el, { delay: 3500 });
  toast.show();
  el.addEventListener('hidden.bs.toast', () => el.remove());
}

function showError(msg)   { showToast(msg, 'danger'); }
function showSuccess(msg) { showToast(msg, 'success'); }
