/**
 * DIGITAL LIBRARY MANAGEMENT SYSTEM — SHARED JAVASCRIPT MODULE
 * Provides CSRF handling, API fetch wrapper, auth guards, toasts, modals,
 * responsive sidebar drawer, dynamic book cover generator, pagination, and badges.
 */

// ──────────────────────────────────────────────────────────────────────────
// 1. CSRF & API Fetch Utility
// ──────────────────────────────────────────────────────────────────────────

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

function getCsrfToken() {
  return getCookie('XSRF-TOKEN');
}

/**
 * Unified fetch wrapper with CSRF injection, session handling, and structured error responses.
 */
async function apiFetch(url, options = {}) {
  const headers = {
    'Accept': 'application/json',
    ...(options.headers || {})
  };

  const method = (options.method || 'GET').toUpperCase();
  if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method)) {
    const csrf = getCsrfToken();
    if (csrf) {
      headers['X-XSRF-TOKEN'] = csrf;
    }
    if (options.body && !(options.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }
  }

  const fetchOptions = {
    ...options,
    method,
    headers,
    credentials: 'same-origin'
  };

  try {
    const response = await fetch(url, fetchOptions);

    if (response.status === 204) {
      return null;
    }

    if (!response.ok) {
      let errorData;
      try {
        errorData = await response.json();
      } catch (e) {
        errorData = { message: response.statusText || `Request failed with status ${response.status}` };
      }

      const error = new Error(errorData.message || 'Operation failed');
      error.status = response.status;
      error.data = errorData;
      throw error;
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return await response.json();
    }
    return await response.text();
  } catch (err) {
    if (err.status === 401 && !window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('register.html') && !window.location.pathname.endsWith('index.html')) {
      showToast('Session expired. Please log in again.', 'warning');
      setTimeout(() => {
        window.location.href = `/login.html?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
      }, 1000);
    }
    throw err;
  }
}

// ──────────────────────────────────────────────────────────────────────────
// 2. Authentication & Session State
// ──────────────────────────────────────────────────────────────────────────

let currentUserCache = null;

async function getCurrentUser(refresh = false) {
  if (currentUserCache && !refresh) {
    return currentUserCache;
  }
  try {
    const user = await apiFetch('/api/auth/me');
    currentUserCache = user;
    return user;
  } catch (err) {
    currentUserCache = null;
    return null;
  }
}

async function requireAuth(expectedRole = null) {
  const user = await getCurrentUser();
  if (!user) {
    const currentPath = window.location.pathname + window.location.search;
    window.location.href = `/login.html?redirect=${encodeURIComponent(currentPath)}`;
    return null;
  }
  if (expectedRole && user.role !== expectedRole) {
    showToast('Unauthorized access to this section', 'danger');
    window.location.href = user.role === 'ROLE_ADMIN' ? '/dashboard.html' : '/browse.html';
    return null;
  }
  return user;
}

async function logout() {
  try {
    await apiFetch('/api/auth/logout', { method: 'POST' });
  } catch (err) {
    console.error('Logout error:', err);
  } finally {
    currentUserCache = null;
    window.location.href = '/login.html';
  }
}

// ──────────────────────────────────────────────────────────────────────────
// 3. UI Notifications (Toasts)
// ──────────────────────────────────────────────────────────────────────────

function ensureToastContainer() {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
  return container;
}

function showToast(message, type = 'info', title = null, duration = 3800) {
  const container = ensureToastContainer();
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  const iconMap = {
    success: 'bi-check-circle-fill',
    warning: 'bi-exclamation-triangle-fill',
    danger: 'bi-x-circle-fill',
    info: 'bi-info-circle-fill'
  };

  const defaultTitles = {
    success: 'Success',
    warning: 'Warning',
    danger: 'Error',
    info: 'Notice'
  };

  const iconClass = iconMap[type] || iconMap.info;
  const toastTitle = title || defaultTitles[type] || 'Notice';

  toast.innerHTML = `
    <i class="bi ${iconClass} toast-icon"></i>
    <div class="toast-content">
      <div class="toast-title">${escapeHtml(toastTitle)}</div>
      <div class="toast-message">${escapeHtml(message)}</div>
    </div>
    <button class="toast-close" aria-label="Close">&times;</button>
  `;

  toast.querySelector('.toast-close').addEventListener('click', () => {
    toast.remove();
  });

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 200);
  }, duration);
}

// ──────────────────────────────────────────────────────────────────────────
// 4. Confirmation Modal
// ──────────────────────────────────────────────────────────────────────────

let confirmModalInstance = null;

function ensureConfirmModal() {
  let modalBackdrop = document.getElementById('global-confirm-modal');
  if (!modalBackdrop) {
    modalBackdrop = document.createElement('div');
    modalBackdrop.id = 'global-confirm-modal';
    modalBackdrop.className = 'modal-backdrop';
    modalBackdrop.innerHTML = `
      <div class="modal-dialog">
        <div class="modal-header">
          <h3 class="modal-title" id="confirm-modal-title">Confirm Action</h3>
          <button type="button" class="modal-close" id="confirm-modal-close">&times;</button>
        </div>
        <div class="modal-body" id="confirm-modal-body">
          Are you sure you want to proceed?
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" id="confirm-modal-cancel">Cancel</button>
          <button type="button" class="btn btn-primary" id="confirm-modal-ok">Confirm</button>
        </div>
      </div>
    `;
    document.body.appendChild(modalBackdrop);
  }
  return modalBackdrop;
}

function showConfirmModal({ title, message, confirmText = 'Confirm', isDanger = false, onConfirm }) {
  const modal = ensureConfirmModal();
  document.getElementById('confirm-modal-title').textContent = title || 'Confirm Action';
  document.getElementById('confirm-modal-body').textContent = message || 'Are you sure?';

  const okBtn = document.getElementById('confirm-modal-ok');
  okBtn.textContent = confirmText;
  okBtn.className = isDanger ? 'btn btn-danger' : 'btn btn-primary';

  const cancelBtn = document.getElementById('confirm-modal-cancel');
  const closeBtn = document.getElementById('confirm-modal-close');

  const closeModal = () => {
    modal.classList.remove('show');
  };

  // Replace buttons to clear previous event listeners
  const newOkBtn = okBtn.cloneNode(true);
  const newCancelBtn = cancelBtn.cloneNode(true);
  const newCloseBtn = closeBtn.cloneNode(true);

  okBtn.parentNode.replaceChild(newOkBtn, okBtn);
  cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);
  closeBtn.parentNode.replaceChild(newCloseBtn, closeBtn);

  newCancelBtn.addEventListener('click', closeModal);
  newCloseBtn.addEventListener('click', closeModal);
  modal.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
  });

  newOkBtn.addEventListener('click', async () => {
    setButtonLoading(newOkBtn, true);
    try {
      if (onConfirm) await onConfirm();
      closeModal();
    } catch (err) {
      showToast(err.message || 'Action failed', 'danger');
    } finally {
      setButtonLoading(newOkBtn, false, confirmText);
    }
  });

  modal.classList.add('show');
}

// ──────────────────────────────────────────────────────────────────────────
// 5. Button Loading State
// ──────────────────────────────────────────────────────────────────────────

function setButtonLoading(button, isLoading, originalHtml = null) {
  if (!button) return;
  if (isLoading) {
    button.dataset.originalContent = button.innerHTML;
    button.disabled = true;
    button.classList.add('loading');
    button.innerHTML = `<span class="spinner"></span> <span>Loading...</span>`;
  } else {
    button.disabled = false;
    button.classList.remove('loading');
    button.innerHTML = originalHtml || button.dataset.originalContent || 'Submit';
  }
}

// ──────────────────────────────────────────────────────────────────────────
// 6. Dynamic Book Cover Generator (CSS Gradients + Initials)
// ──────────────────────────────────────────────────────────────────────────

function getCategoryGradientClass(category) {
  const cat = (category || '').toLowerCase().trim();
  if (cat.includes('java')) return 'bg-gradient-java';
  if (cat.includes('program')) return 'bg-gradient-programming';
  if (cat.includes('database') || cat.includes('sql')) return 'bg-gradient-database';
  if (cat.includes('web') || cat.includes('html') || cat.includes('front')) return 'bg-gradient-web-dev';
  if (cat.includes('computer') || cat.includes('cs') || cat.includes('algo')) return 'bg-gradient-cs';
  if (cat.includes('fiction')) return 'bg-gradient-fiction';
  if (cat.includes('science')) return 'bg-gradient-science';
  if (cat.includes('history')) return 'bg-gradient-history';
  return 'bg-gradient-default';
}

function getInitials(title) {
  if (!title) return 'BK';
  const clean = title.replace(/[^a-zA-Z0-9\s]/g, '').trim();
  const words = clean.split(/\s+/).filter(w => w.length > 0);
  if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
  if (words.length >= 2) return (words[0][0] + words[1][0]).toUpperCase();
  return 'BK';
}

function generateBookCover(title, category, isLarge = false) {
  const initials = getInitials(title);
  const gradClass = getCategoryGradientClass(category);
  const sizeClass = isLarge ? 'book-cover-lg' : '';

  return `
    <div class="book-cover ${gradClass} ${sizeClass}" title="${escapeHtml(title || '')}">
      <div class="book-cover-initials">${escapeHtml(initials)}</div>
      <div class="book-cover-category">${escapeHtml(category || 'General')}</div>
    </div>
  `;
}

// ──────────────────────────────────────────────────────────────────────────
// 7. Status Badges
// ──────────────────────────────────────────────────────────────────────────

function renderBadge(status) {
  const s = (status || '').toUpperCase();
  switch (s) {
    case 'ISSUED':
      return `<span class="badge badge-info"><i class="bi bi-clock-history"></i> Issued</span>`;
    case 'OVERDUE':
      return `<span class="badge badge-danger"><i class="bi bi-exclamation-octagon"></i> Overdue</span>`;
    case 'RETURNED':
      return `<span class="badge badge-success"><i class="bi bi-check2-circle"></i> Returned</span>`;
    case 'ACTIVE':
      return `<span class="badge badge-warning"><i class="bi bi-bookmark-fill"></i> Active</span>`;
    case 'FULFILLED':
      return `<span class="badge badge-success"><i class="bi bi-check-all"></i> Fulfilled</span>`;
    case 'CANCELLED':
      return `<span class="badge badge-neutral"><i class="bi bi-x-circle"></i> Cancelled</span>`;
    case 'OPEN':
      return `<span class="badge badge-warning"><i class="bi bi-envelope-open"></i> Open</span>`;
    case 'RESOLVED':
      return `<span class="badge badge-success"><i class="bi bi-check2"></i> Resolved</span>`;
    case 'PAID':
      return `<span class="badge badge-success"><i class="bi bi-cash-stack"></i> Paid</span>`;
    case 'UNPAID':
      return `<span class="badge badge-danger"><i class="bi bi-cash"></i> Unpaid</span>`;
    case 'ROLE_ADMIN':
      return `<span class="badge badge-primary"><i class="bi bi-shield-lock"></i> Admin</span>`;
    case 'ROLE_USER':
      return `<span class="badge badge-neutral"><i class="bi bi-person"></i> Member</span>`;
    default:
      return `<span class="badge badge-neutral">${escapeHtml(status)}</span>`;
  }
}

// ──────────────────────────────────────────────────────────────────────────
// 8. Pagination Component
// ──────────────────────────────────────────────────────────────────────────

function renderPagination(pageData, onPageClickFuncName) {
  if (!pageData || pageData.totalPages <= 1) return '';

  const current = pageData.number;
  const total = pageData.totalPages;
  let items = [];

  // Prev
  items.push(`
    <li class="page-item ${current === 0 ? 'disabled' : ''}">
      <button class="page-link" onclick="${onPageClickFuncName}(${current - 1})" aria-label="Previous">
        <i class="bi bi-chevron-left"></i>
      </button>
    </li>
  `);

  let start = Math.max(0, current - 2);
  let end = Math.min(total - 1, current + 2);

  if (start > 0) {
    items.push(`<li class="page-item"><button class="page-link" onclick="${onPageClickFuncName}(0)">1</button></li>`);
    if (start > 1) {
      items.push(`<li class="page-item disabled"><span class="page-link">…</span></li>`);
    }
  }

  for (let i = start; i <= end; i++) {
    items.push(`
      <li class="page-item ${i === current ? 'active' : ''}">
        <button class="page-link" onclick="${onPageClickFuncName}(${i})">${i + 1}</button>
      </li>
    `);
  }

  if (end < total - 1) {
    if (end < total - 2) {
      items.push(`<li class="page-item disabled"><span class="page-link">…</span></li>`);
    }
    items.push(`<li class="page-item"><button class="page-link" onclick="${onPageClickFuncName}(${total - 1})">${total}</button></li>`);
  }

  // Next
  items.push(`
    <li class="page-item ${current >= total - 1 ? 'disabled' : ''}">
      <button class="page-link" onclick="${onPageClickFuncName}(${current + 1})" aria-label="Next">
        <i class="bi bi-chevron-right"></i>
      </button>
    </li>
  `);

  return `<ul class="pagination">${items.join('')}</ul>`;
}

// ──────────────────────────────────────────────────────────────────────────
// 9. Empty State & Skeleton Loaders
// ──────────────────────────────────────────────────────────────────────────

function renderEmptyState(title, subtitle, icon = 'bi-folder-x', actionHtml = '') {
  return `
    <div class="empty-state">
      <i class="bi ${icon} empty-state-icon"></i>
      <h3 class="empty-state-title">${escapeHtml(title)}</h3>
      <p class="empty-state-text">${escapeHtml(subtitle)}</p>
      ${actionHtml}
    </div>
  `;
}

function renderSkeletonRows(colCount, rowCount = 5) {
  let rows = '';
  for (let i = 0; i < rowCount; i++) {
    let cells = '';
    for (let j = 0; j < colCount; j++) {
      cells += `<td><div class="skeleton skeleton-text"></div></td>`;
    }
    rows += `<tr>${cells}</tr>`;
  }
  return rows;
}

// ──────────────────────────────────────────────────────────────────────────
// 10. Layout & Navigation Initialization
// ──────────────────────────────────────────────────────────────────────────

function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

async function initLayout(activeNavId) {
  // Mobile drawer bindings
  const mobileBtn = document.getElementById('mobile-menu-btn');
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('sidebar-overlay');
  const closeBtn = document.getElementById('sidebar-close-btn');

  if (mobileBtn && sidebar && overlay) {
    mobileBtn.addEventListener('click', () => {
      sidebar.classList.add('open');
      overlay.classList.add('active');
    });
    const closeDrawer = () => {
      sidebar.classList.remove('open');
      overlay.classList.remove('active');
    };
    if (closeBtn) closeBtn.addEventListener('click', closeDrawer);
    overlay.addEventListener('click', closeDrawer);
  }

  // Highlight active link
  if (activeNavId) {
    const activeLink = document.getElementById(`nav-${activeNavId}`);
    if (activeLink) activeLink.classList.add('active');
  }

  // Fetch current user & update user snippet
  const user = await getCurrentUser();
  const userSection = document.getElementById('sidebar-user-section');
  const adminNav = document.getElementById('admin-nav-section');
  const userNav = document.getElementById('user-nav-section');

  if (user) {
    if (userSection) {
      userSection.innerHTML = `
        <div class="user-snippet">
          <div class="user-avatar">${getInitials(user.name)}</div>
          <div class="user-info">
            <div class="user-name" title="${escapeHtml(user.name)}">${escapeHtml(user.name)}</div>
            <div class="user-role-badge">${user.role === 'ROLE_ADMIN' ? 'Administrator' : 'Library Member'}</div>
          </div>
          <button class="btn-logout" id="sidebar-logout-btn" title="Sign out">
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
      `;
      document.getElementById('sidebar-logout-btn')?.addEventListener('click', logout);
    }

    if (user.role === 'ROLE_ADMIN') {
      if (adminNav) adminNav.style.display = 'block';
    } else {
      if (adminNav) adminNav.style.display = 'none';
    }
  } else {
    // Unauthenticated state
    if (adminNav) adminNav.style.display = 'none';
    if (userSection) {
      userSection.innerHTML = `
        <a href="/login.html" class="btn btn-primary btn-sm" style="width: 100%;">
          <i class="bi bi-box-arrow-in-right"></i> Sign In
        </a>
      `;
    }
  }
}
