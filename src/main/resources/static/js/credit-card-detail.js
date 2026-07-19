const params = new URLSearchParams(window.location.search);
const cardId = Number(params.get('card'));
const customerId = params.get('customer');

let minPay = '0.00';

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function utilClass(pct) {
    if (pct < 30) return 'low';
    if (pct < 70) return 'medium';
    return 'high';
}

function calcMin(balance) {
    if (balance <= 0) return '0.00';
    return Math.max(10, balance * 0.02).toFixed(2);
}

function renderCard(c) {
    const available = Number(c.creditLimit) - Number(c.balance);
    const pct = Math.min(100, Math.round((c.balance / c.creditLimit) * 100));
    const cls = utilClass(pct);

    document.getElementById('cc-id').textContent = `#CC${String(cardId).padStart(3, '0')}`;
    document.getElementById('cc-balance').textContent = fmt(c.balance);
    document.getElementById('cc-limit').textContent = fmt(c.creditLimit);
    document.getElementById('cc-available').textContent = fmt(available);
    document.getElementById('cc-apr').textContent = `${(c.apr * 100).toFixed(2)}%`;
    document.getElementById('cc-util-pct').textContent = `${pct}%`;

    const fill = document.getElementById('cc-util-fill');
    fill.style.width = `${pct}%`;
    fill.className = `cc-util-fill ${cls}`;

    minPay = calcMin(Number(c.balance));
    document.getElementById('min-label').textContent = `Min. payment: ${fmt(minPay)}`;
}

async function charge() {
    const merchantId = Number(document.getElementById('merchant-select').value);
    const amount = document.getElementById('purchase-amount').value;
    const msgEl = document.getElementById('purchase-msg');

    if (!amount || Number(amount) <= 0) {
        msgEl.textContent = 'Enter a valid amount.';
        msgEl.className = 'cc-action-msg error';
        return;
    }

    try {
        await API.creditCards.purchase(cardId, { amount: Number(amount), merchantId });
        msgEl.textContent = `Charged ${fmt(amount)}.`;
        msgEl.className = 'cc-action-msg success';
        document.getElementById('purchase-amount').value = '';
        const c = await API.creditCards.getById(cardId);
        renderCard(c);
    } catch (e) {
        msgEl.textContent = e.message;
        msgEl.className = 'cc-action-msg error';
    }
}

async function pay() {
    const fromAccountId = Number(document.getElementById('account-select').value);
    const amount = document.getElementById('pay-amount').value;
    const msgEl = document.getElementById('pay-msg');

    if (!amount || Number(amount) <= 0) {
        msgEl.textContent = 'Enter a valid amount.';
        msgEl.className = 'cc-action-msg error';
        return;
    }

    try {
        await API.creditCards.pay(cardId, { fromAccountId, amount: Number(amount) });
        msgEl.textContent = `Paid ${fmt(amount)}.`;
        msgEl.className = 'cc-action-msg success';
        document.getElementById('pay-amount').value = '';
        const c = await API.creditCards.getById(cardId);
        renderCard(c);
    } catch (e) {
        msgEl.textContent = e.message;
        msgEl.className = 'cc-action-msg error';
    }
}

async function load() {
    if (!cardId || !customerId) return;
    document.getElementById('back-btn').href = `/pages/credit-cards.html?customer=${customerId}`;

    const [card, merchants, accounts] = await Promise.all([
        API.creditCards.getById(cardId),
        API.merchants.getAll(),
        API.accounts.getByCustomer(customerId)
    ]);

    renderCard(card);

    document.getElementById('merchant-select').innerHTML = merchants.map(m =>
        `<option value="${m.id}">${m.name} (${m.category})</option>`
    ).join('');

    const chequing = accounts.filter(a => a.type === 'chequing');
    document.getElementById('account-select').innerHTML = chequing.map(a =>
        `<option value="${a.id}">Chequing #A${String(a.id).padStart(3, '0')} (${fmt(a.balance)})</option>`
    ).join('');

    document.getElementById('charge-btn').addEventListener('click', charge);
    document.getElementById('min-btn').addEventListener('click', () => {
        document.getElementById('pay-amount').value = minPay;
    });
    document.getElementById('pay-btn').addEventListener('click', pay);
}

load();
