const params = new URLSearchParams(window.location.search);
const accountId = params.get('account');

const searchInput = document.getElementById('merchant-search');
const monthPicker = document.getElementById('month-picker');
const txnBody = document.getElementById('txn-body');
const summaryIn = document.getElementById('summary-in');
const summaryOut = document.getElementById('summary-out');

let allTransactions = [];

function render() {
    const month = monthPicker.value;
    const search = searchInput.value.trim().toLowerCase();

    const filtered = allTransactions.filter(t => {
        const matchesMonth = !month || t.date.startsWith(month);
        const matchesSearch = !search || t.description.toLowerCase().includes(search);
        return matchesMonth && matchesSearch;
    });

    txnBody.innerHTML = '';

    if (filtered.length === 0) {
        txnBody.innerHTML = '<tr class="empty-row"><td colspan="4">No transactions</td></tr>';
    } else {
        for (const t of filtered) {
            const tr = document.createElement('tr');
            const positive = t.amount >= 0;
            tr.innerHTML = `
                <td>${t.date}</td>
                <td class="${t.description === 'Deposited' ? 'amount-positive' : t.description === 'Withdrawn' ? 'amount-negative' : t.description === 'Transfer' ? (t.amount < 0 ? 'amount-negative' : 'amount-positive') : ''}">${t.description}</td>
                <td class="${positive ? 'amount-positive' : 'amount-negative'}">${positive ? '+' : ''}${t.amount.toFixed(2)}</td>
                <td class="num">${t.balance.toFixed(2)}</td>
            `;
            txnBody.appendChild(tr);
        }
    }

    const totalIn = filtered.filter(t => t.amount > 0).reduce((sum, t) => sum + t.amount, 0);
    const totalOut = filtered.filter(t => t.amount < 0).reduce((sum, t) => sum + t.amount, 0);
    summaryIn.textContent = `+$${totalIn.toFixed(2)} in`;
    summaryOut.textContent = `-$${Math.abs(totalOut).toFixed(2)} out`;
}

searchInput.addEventListener('input', render);
monthPicker.addEventListener('change', render);

function renderActionNav(account) {
    const isChequing = account.type === 'chequing';
    document.getElementById('txn-action-nav').innerHTML = `
        <a href="/deposit.html?account=${accountId}" class="btn-action">+ Deposit</a>
        ${isChequing ? `<a href="/withdraw.html?account=${accountId}" class="btn-action btn-action-secondary">− Withdraw</a>` : ''}
        <a href="/transfer.html?account=${accountId}" class="btn-action btn-action-secondary">Transfer →</a>
        <a href="/summary.html?account=${accountId}" class="btn-action btn-action-secondary">Summary</a>
    `;
}

async function load() {
    if (!accountId) {
        txnBody.innerHTML = '<tr class="empty-row"><td colspan="4">No account specified</td></tr>';
        return;
    }

    try {
        const account = await API.accounts.getById(accountId);
        const transactions = await API.transactions.getByAccountId(accountId);

        document.getElementById('account-type').textContent =
            account.type.charAt(0).toUpperCase() + account.type.slice(1);
        document.getElementById('account-id').textContent = `#A${String(account.id).padStart(3, '0')}`;
        document.getElementById('account-balance').textContent =
            '$' + Number(account.balance).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        document.getElementById('back-btn').href = `/dashboard.html?customer=${account.customerId}`;

        renderActionNav(account);
        const chronological = [...transactions].sort((a, b) => a.id - b.id);
        const totalAmount = chronological.reduce((sum, t) => sum + t.amount, 0);
        let runningBalance = account.balance - totalAmount;

        const withBalances = chronological.map(t => {
            runningBalance += t.amount;
            return {
                date: t.date,
                description: t.description,
                amount: t.amount,
                balance: runningBalance
            };
        });

        allTransactions = withBalances.reverse();
        render();
    } catch (e) {
        console.error('Failed to load transactions', e);
        txnBody.innerHTML = `<tr class="empty-row"><td colspan="4">Error: ${e.message}</td></tr>`;
    }
}

load();
