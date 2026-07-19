const params = new URLSearchParams(window.location.search);
const accountId = params.get('account');

document.getElementById('back-btn').href = `/pages/transactions.html?account=${accountId}`;

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function load() {
    if (!accountId) { showError('No account specified.'); return; }
    try {
        const account = await API.accounts.getById(accountId);
        document.getElementById('account-label').textContent =
            `${account.type.charAt(0).toUpperCase() + account.type.slice(1)} #A${String(account.id).padStart(3, '0')}`;
        document.getElementById('account-balance').textContent = fmt(account.balance);
    } catch (e) {
        showError('Failed to load account: ' + e.message);
    }
}

async function handleSubmit(e) {
    e.preventDefault();
    const input = document.getElementById('amount');
    const amount = Number(input.value);
    if (!input.value || isNaN(amount) || amount <= 0) {
        showError('Enter a valid amount greater than $0.00');
        return;
    }
    const btn = document.getElementById('submit-btn');
    btn.disabled = true;
    btn.textContent = 'Processing...';
    try {
        await API.accounts.withdraw(accountId, amount);
        window.location.href = `/pages/transactions.html?account=${accountId}`;
    } catch (e) {
        showError('Withdraw failed: ' + e.message);
        btn.disabled = false;
        btn.textContent = 'Withdraw';
    }
}

function showError(msg) {
    document.getElementById('form-error').textContent = msg;
}

document.getElementById('action-form').addEventListener('submit', handleSubmit);
load();
