const params = new URLSearchParams(window.location.search);
const customerId = params.get('customer');

function initials(name) {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
}

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function load() {
    if (!customerId) {
        document.getElementById('customer-name').textContent = 'No ?customer= in URL';
        return;
    }

    try {
        const c = await API.customers.getById(customerId);
        document.getElementById('customer-name').textContent = c.name;
        document.getElementById('customer-email').textContent = c.email;
        document.getElementById('customer-dob').textContent = c.dateOfBirth ?? '';
        document.getElementById('customer-since').textContent = c.memberSince ?? '';
        document.getElementById('customer-phone').textContent = c.phone ?? '';
        document.getElementById('avatar').textContent = initials(c.name);
    } catch (e) {
        console.error('Failed to load customer', e);
        document.getElementById('customer-name').textContent = 'Error: ' + e.message;
    }

    try {
        const accounts = await API.accounts.getByCustomer(customerId);
        const grid = document.getElementById('accounts-grid');
        grid.innerHTML = accounts.map(a => `
            <div class="account-card">
                <div class="account-top">
                    <span class="account-type">${a.type.charAt(0).toUpperCase() + a.type.slice(1)}</span>
                    <span class="account-id">#A${String(a.id).padStart(3, '0')}</span>
                </div>
                <div class="account-balance">${fmt(a.balance)}</div>
                <a href="/pages/transactions.html?account=${a.id}" class="btn-view">View Transactions →</a>
                <a href="/pages/analytics.html?account=${a.id}" class="btn-view" style="color:#C9A227;font-size:11px">Analytics →</a>
            </div>
        `).join('');
    } catch (e) {
        console.error('Failed to load accounts', e);
    }

    try {
        const loans = await API.loans.getByCustomer(customerId);
        renderLoans(loans);
    } catch (e) {
        console.error('Failed to load loans', e);
    }

    try {
        const cards = await API.creditCards.getByCustomer(customerId);
        renderCredit(cards[0] ?? null);
    } catch (e) {
        console.error('Failed to load credit cards', e);
    }

    try {
        const flags = await API.fraud.getByCustomer(customerId);
        renderFraud(flags.length);
    } catch (e) {
        console.error('Failed to load fraud flags', e);
    }

}

function renderLoans(loans) {
    const el = document.getElementById('loans-list');
    if (!loans.length) {
        el.innerHTML = `
            <div class="loan-row" style="justify-content:center">
                <span class="loan-id" style="font-style:italic">No active loans</span>
            </div>`;
        return;
    }
    el.innerHTML = loans.map(l => `
        <div class="loan-row">
            <span class="loan-type">${l.type.charAt(0).toUpperCase() + l.type.slice(1)}</span>
            <a href="/pages/loans.html?customer=${customerId}&loan=${l.id}" class="btn-view-sm">View →</a>
        </div>
    `).join('');
}

function renderFraud(count) {
    const el = document.getElementById('fraud-alert');
    if (!count) {
        el.innerHTML = `
            <div class="alert-row" style="background:#F4F6F9;justify-content:center">
                <span class="alert-text" style="color:#3B6EA5;font-weight:500;font-style:italic">No fraud alerts</span>
            </div>`;
        return;
    }
    el.innerHTML = `
        <div class="alert-row">
            <span class="alert-text">${count} Fraud Alert${count > 1 ? 's' : ''}</span>
            <a href="/pages/fraud.html?customer=${customerId}" class="btn-view-sm">View →</a>
        </div>
    `;
}

function renderCredit(card) {
    const el = document.getElementById('credit-block');
    if (!card) {
        el.innerHTML = `
            <div class="credit-block" style="align-items:center;justify-content:center">
                <span class="credit-pct" style="font-style:italic">No credit card on file</span>
            </div>`;
        return;
    }
    const pct = Math.round((card.balance / card.creditLimit) * 100);
    el.innerHTML = `
        <div class="credit-block">
            <div class="credit-header">
                <span class="credit-label">Credit Card</span>
                <span class="credit-ratio">${fmt(card.balance)} / ${fmt(card.creditLimit)}</span>
            </div>
            <div class="credit-bar-track">
                <div class="credit-bar-fill" style="width: ${pct}%"></div>
            </div>
            <span class="credit-pct">${pct}% used</span>
            <a href="/pages/credit-cards.html?customer=${customerId}" class="btn-view-sm" style="margin-top:4px">Manage →</a>
        </div>
    `;
}

const COOLDOWN_MS = 60 * 1000;
const COOLDOWN_KEY = 'interest_cooldown_end';

async function runInterestCycle() {
    const btn = document.getElementById('btn-interest');
    if (btn.disabled) return;
    try {
        await API.interest.run();
        const end = Date.now() + COOLDOWN_MS;
        localStorage.setItem(COOLDOWN_KEY, end);
        startCooldown(btn, end);
    } catch (e) {
        console.error('Interest cycle failed', e);
    }
}

function startCooldown(btn, end) {
    btn.disabled = true;
    btn.classList.add('cooling');
    tick(btn, end);
}

function tick(btn, end) {
    const remaining = Math.ceil((end - Date.now()) / 1000);
    if (remaining <= 0) {
        localStorage.removeItem(COOLDOWN_KEY);
        btn.disabled = false;
        btn.classList.remove('cooling');
        btn.textContent = 'Run Interest Cycle';
        return;
    }
    btn.textContent = `Cooling down (${remaining}s)`;
    setTimeout(() => tick(btn, end), 1000);
}

function restoreCooldown() {
    const stored = localStorage.getItem(COOLDOWN_KEY);
    if (!stored) return;
    const end = Number(stored);
    if (end > Date.now()) {
        startCooldown(document.getElementById('btn-interest'), end);
    } else {
        localStorage.removeItem(COOLDOWN_KEY);
    }
}

document.getElementById('btn-interest').addEventListener('click', runInterestCycle);
restoreCooldown();

load();
