const params = new URLSearchParams(window.location.search);
const accountId = params.get('account');

document.getElementById('back-btn').href = `/pages/transactions.html?account=${accountId}`;
document.getElementById('generated-date').textContent = new Date().toLocaleDateString('en-CA', {
    year: 'numeric', month: 'long', day: 'numeric'
});

function fmt(n) {
    return '$' + Math.abs(Number(n)).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function monthLabel(yyyymm) {
    const [y, m] = yyyymm.split('-');
    return new Date(Number(y), Number(m) - 1).toLocaleString('en-CA', { month: 'long', year: 'numeric' });
}

let allWithBalances = [];
let allWithBalancesById = {};
let activeType = 'all';
let activeTab = 'statement';
let backendSummary = null;
let chartInstance = null;

async function getFiltered() {
    const month = document.getElementById('month-picker').value;
    let base;
    if (month) {
        const raw = await API.transactions.getByAccountId(accountId, month);
        base = raw.map(t => allWithBalancesById[t.id] || t);
    } else {
        base = allWithBalances;
    }
    return base.filter(t =>
        activeType === 'all'
        || (activeType === 'in'  && t.amount > 0)
        || (activeType === 'out' && t.amount < 0)
    );
}

function renderStats({ openingBalance, closingBalance, cashIn, cashOut, totalAmount, transactions }) {
    document.getElementById('opening-balance').textContent = fmt(openingBalance);
    document.getElementById('closing-balance').textContent = fmt(closingBalance);
    document.getElementById('stat-in').textContent    = fmt(cashIn);
    document.getElementById('stat-out').textContent   = fmt(cashOut);
    document.getElementById('stat-count').textContent = transactions;
    const net = Number(totalAmount);
    const netEl = document.getElementById('stat-net');
    netEl.textContent = (net >= 0 ? '+' : '-') + fmt(net);
}

function renderTable(filtered) {
    const tbody = document.getElementById('statement-body');
    tbody.innerHTML = '';
    if (filtered.length === 0) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">No transactions for this period</td></tr>';
        return;
    }
    [...filtered].reverse().forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${t.date}</td>
            <td>${t.description}</td>
            <td class="num">${t.amount < 0 ? fmt(t.amount) : ''}</td>
            <td class="num">${t.amount > 0 ? fmt(t.amount) : ''}</td>
            <td class="num">${fmt(t.balance)}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function render() {
    const month = document.getElementById('month-picker').value;
    const filtered = await getFiltered();

    document.getElementById('period-label').textContent = month ? monthLabel(month) : 'All Time';

    if (month || activeType !== 'all') {
        const cashIn  = filtered.filter(t => t.amount > 0).reduce((s, t) => s + t.amount, 0);
        const cashOut = filtered.filter(t => t.amount < 0).reduce((s, t) => s + t.amount, 0);
        renderStats({
            openingBalance: filtered.length > 0 ? filtered[filtered.length - 1].balance - filtered[filtered.length - 1].amount : 0,
            closingBalance: filtered.length > 0 ? filtered[0].balance : 0,
            cashIn,
            cashOut,
            totalAmount: cashIn + cashOut,
            transactions: filtered.length
        });
    } else if (backendSummary) {
        renderStats(backendSummary);
    }

    renderTable(filtered);
}

function buildCashFlowData() {
    const byMonth = {};
    allWithBalances.forEach(t => {
        const m = t.date.slice(0, 7);
        if (!byMonth[m]) byMonth[m] = { cashIn: 0, cashOut: 0 };
        if (t.amount > 0) byMonth[m].cashIn  += t.amount;
        else              byMonth[m].cashOut += Math.abs(t.amount);
    });
    const months = Object.keys(byMonth).sort();
    return {
        labels: months.map(monthLabel),
        cashIn: months.map(m => +byMonth[m].cashIn.toFixed(2)),
        cashOut: months.map(m => +byMonth[m].cashOut.toFixed(2))
    };
}

function buildDescriptionData() {
    const byDesc = {};
    allWithBalances.forEach(t => {
        const d = t.description;
        if (!byDesc[d]) byDesc[d] = 0;
        byDesc[d] += Math.abs(t.amount);
    });
    const descs = Object.keys(byDesc).sort((a, b) => byDesc[b] - byDesc[a]);
    return {
        labels: descs,
        values: descs.map(d => +byDesc[d].toFixed(2))
    };
}

function renderChart() {
    const type = document.getElementById('chart-type').value;
    const canvas = document.getElementById('main-chart');
    if (chartInstance) { chartInstance.destroy(); chartInstance = null; }

    if (type === 'cashflow') {
        const { labels, cashIn, cashOut } = buildCashFlowData();
        chartInstance = new Chart(canvas, {
            type: 'bar',
            data: {
                labels,
                datasets: [
                    { label: 'Cash In',  data: cashIn,  backgroundColor: '#3c6ea5', barPercentage: 0.4 },
                    { label: 'Cash Out', data: cashOut, backgroundColor: '#daca9d', barPercentage: 0.4 }
                ]
            },
            options: chartOptions('Amount (CAD)')
        });
    } else {
        const { labels, values } = buildDescriptionData();
        chartInstance = new Chart(canvas, {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    label: 'Total Amount',
                    data: values,
                    backgroundColor: '#3c6ea5',
                    barPercentage: 0.5
                }]
            },
            options: chartOptions('Amount (CAD)')
        });
    }
}

function chartOptions(yLabel) {
    return {
        responsive: true,
        plugins: {
            legend: { labels: { font: { family: 'IBM Plex Sans', size: 12 }, color: '#111' } },
            tooltip: {
                callbacks: { label: ctx => ' $' + ctx.parsed.y.toLocaleString('en-CA', { minimumFractionDigits: 2 }) }
            }
        },
        scales: {
            x: { ticks: { font: { family: 'IBM Plex Mono', size: 11 }, color: '#374151' }, grid: { color: '#e5e7eb' } },
            y: {
                title: { display: true, text: yLabel, font: { family: 'IBM Plex Sans', size: 11 }, color: '#6b7280' },
                ticks: { font: { family: 'IBM Plex Mono', size: 11 }, color: '#374151',
                    callback: v => '$' + v.toLocaleString('en-CA') },
                grid: { color: '#e5e7eb' }
            }
        }
    };
}

const statementControls = document.querySelector('.statement-controls');

document.querySelectorAll('.nav-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        activeTab = tab.dataset.tab;
        const onStatement = activeTab === 'statement';
        document.getElementById('tab-statement').style.display = onStatement ? 'block' : 'none';
        document.getElementById('tab-visuals').style.display   = onStatement ? 'none'  : 'block';
        statementControls.style.display = onStatement ? 'flex' : 'none';
        if (!onStatement) renderChart();
    });
});

document.getElementById('chart-type').addEventListener('change', renderChart);

document.getElementById('month-picker').addEventListener('change', render);

document.querySelectorAll('.type-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.type-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        activeType = btn.dataset.type;
        render();
    });
});

function populateMonthPicker(transactions) {
    const months = [...new Set(transactions.map(t => t.date.slice(0, 7)))].sort().reverse();
    const select = document.getElementById('month-picker');
    months.forEach(m => {
        const opt = document.createElement('option');
        opt.value = m;
        opt.textContent = monthLabel(m);
        select.appendChild(opt);
    });
}

async function load() {
    if (!accountId) return;
    const [account, transactions, summary] = await Promise.all([
        API.accounts.getById(accountId),
        API.transactions.getByAccountId(accountId),
        API.accounts.getSummary(accountId)
    ]);

    const customer = await API.customers.getById(account.customerId);

    document.getElementById('customer-name').textContent = customer.name;
    document.getElementById('account-label').textContent =
        `${account.type.charAt(0).toUpperCase() + account.type.slice(1)} #A${String(account.id).padStart(3, '0')}`;

    const chronological = [...transactions].sort((a, b) => a.id - b.id);
    const total = chronological.reduce((s, t) => s + t.amount, 0);
    let running = account.balance - total;
    allWithBalancesById = {};
    allWithBalances = chronological.map(t => {
        running += t.amount;
        const entry = { id: t.id, date: t.date, description: t.description, amount: t.amount, balance: running };
        allWithBalancesById[t.id] = entry;
        return entry;
    }).reverse();

    backendSummary = summary;
    populateMonthPicker(allWithBalances);
    render();
}

load();
