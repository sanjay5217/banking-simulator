const params = new URLSearchParams(window.location.search);
const accountId = params.get('account');

document.getElementById('back-btn').href = `/transactions.html?account=${accountId}`;

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function load() {
    if (!accountId) return;
    try {
        const account = await API.accounts.getById(accountId);
        document.getElementById('account-label').textContent =
            `${account.type.charAt(0).toUpperCase() + account.type.slice(1)} #A${String(account.id).padStart(3, '0')}`;
        document.getElementById('account-balance').textContent = fmt(account.balance);

        const [accounts, allCustomers] = await Promise.all([
            API.accounts.getByCustomer(account.customerId),
            API.customers.getAll()
        ]);

        const internalSelect = document.getElementById('internal-to');
        accounts.filter(a => a.id !== account.id).forEach(a => {
            const opt = document.createElement('option');
            opt.value = a.id;
            opt.textContent = `${a.type.charAt(0).toUpperCase() + a.type.slice(1)} #A${String(a.id).padStart(3, '0')} — ${fmt(a.balance)}`;
            internalSelect.appendChild(opt);
        });

        const customerSelect = document.getElementById('external-customer');
        allCustomers.filter(c => c.id !== account.customerId).forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = c.name;
            customerSelect.appendChild(opt);
        });
    } catch (e) {
        console.error('Failed to load account', e);
    }
}

document.getElementById('external-customer').addEventListener('change', async (e) => {
    const customerId = e.target.value;
    const toSelect = document.getElementById('external-to');
    toSelect.innerHTML = '<option value="">Select account...</option>';
    toSelect.disabled = !customerId;
    if (!customerId) return;
    try {
        const accounts = await API.accounts.getByCustomer(customerId);
        accounts.forEach(a => {
            const opt = document.createElement('option');
            opt.value = a.id;
            opt.textContent = `${a.type.charAt(0).toUpperCase() + a.type.slice(1)} #A${String(a.id).padStart(3, '0')} — ${fmt(a.balance)}`;
            toSelect.appendChild(opt);
        });
    } catch (err) {
        console.error('Failed to load accounts', err);
    }
});

document.getElementById('internal-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const toId = document.getElementById('internal-to').value;
    const errEl = document.getElementById('internal-error');
    if (!toId) { errEl.textContent = 'Select a destination account'; return; }
    const amount = Number(document.getElementById('internal-amount').value);
    if (!amount || amount <= 0) { errEl.textContent = 'Enter a valid amount'; return; }
    errEl.textContent = '';
    const btn = document.getElementById('internal-btn');
    btn.disabled = true;
    btn.textContent = 'Processing...';
    try {
        await API.transfers.internal({ fromAccountId: Number(accountId), toAccountId: Number(toId), amount });
        window.location.href = `/transactions.html?account=${accountId}`;
    } catch (e) {
        errEl.textContent = 'Transfer failed: ' + e.message;
        btn.disabled = false;
        btn.textContent = 'Transfer';
    }
});

document.getElementById('external-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const toId = document.getElementById('external-to').value;
    const errEl = document.getElementById('external-error');
    if (!document.getElementById('external-customer').value) { errEl.textContent = 'Select a customer'; return; }
    if (!toId) { errEl.textContent = 'Select a destination account'; return; }
    const amount = Number(document.getElementById('external-amount').value);
    if (!amount || amount <= 0) { errEl.textContent = 'Enter a valid amount'; return; }
    errEl.textContent = '';
    const btn = document.getElementById('external-btn');
    btn.disabled = true;
    btn.textContent = 'Processing...';
    try {
        await API.transfers.external(Number(toId), { fromAccountId: Number(accountId), amount });
        window.location.href = `/transactions.html?account=${accountId}`;
    } catch (e) {
        errEl.textContent = 'Transfer failed: ' + e.message;
        btn.disabled = false;
        btn.textContent = 'Transfer';
    }
});

load();
