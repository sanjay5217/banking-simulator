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
        document.getElementById('customer-dob').textContent = c.dateOfBirth ?? '—';
        document.getElementById('customer-since').textContent = c.memberSince ?? '—';
        document.getElementById('customer-phone').textContent = c.phone ?? '—';
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
                <a href="/transactions.html?account=${a.id}" class="btn-view">View Transactions →</a>
            </div>
        `).join('');
    } catch (e) {
        console.error('Failed to load accounts', e);
    }

}

function renderLoans(loans) {
    const el = document.getElementById('loans-list');
    if (!loans.length) {
        el.innerHTML = '<p class="empty">No active loans</p>';
        return;
    }
    el.innerHTML = loans.map(l => `
        <div class="loan-row">
            <div class="loan-left">
                <span class="loan-type">${l.type.charAt(0).toUpperCase() + l.type.slice(1)}</span>
                <span class="loan-id">#L${String(l.id).padStart(3, '0')}</span>
            </div>
            <div class="loan-right">
                <span class="loan-amount">${fmt(l.principal)}</span>
                <span class="loan-rate">@ ${(l.interestRate * 100).toFixed(2)}%</span>
                <span class="loan-term">${l.termMonths / 12}yr</span>
            </div>
        </div>
    `).join('');
}

function renderFraud(count) {
    const el = document.getElementById('fraud-alert');
    if (!count) {
        el.innerHTML = '<p class="empty">No fraud alerts</p>';
        return;
    }
    el.innerHTML = `
        <div class="alert-row">
            <span class="alert-icon">⚠</span>
            <span class="alert-text">${count} Fraud Alert${count > 1 ? 's' : ''}</span>
            <a href="/fraud.html?customer=${customerId}" class="btn-view-sm">View →</a>
        </div>
    `;
}

function renderCredit(card) {
    const el = document.getElementById('credit-block');
    if (!card) {
        el.innerHTML = '<p class="empty">No credit card</p>';
        return;
    }
    const pct = Math.round((card.balance / card.creditLimit) * 100);
    el.innerHTML = `
        <div class="credit-block">
            <div class="credit-header">
                <span class="credit-label">💳 Credit Card</span>
                <span class="credit-ratio">${fmt(card.balance)} / ${fmt(card.creditLimit)}</span>
            </div>
            <div class="credit-bar-track">
                <div class="credit-bar-fill" style="width: ${pct}%"></div>
            </div>
            <span class="credit-pct">${pct}% used</span>
        </div>
    `;
}

load();
