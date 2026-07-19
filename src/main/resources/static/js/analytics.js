const params = new URLSearchParams(window.location.search);
const initAccountId = params.get('account');

const COLORS = [
    '#0B2E59', '#3B6EA5', '#C9A227', '#d97706', '#2563eb',
    '#1E7A46', '#B3261E', '#7c3aed', '#ec4899', '#14b8a6'
];

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

let chart = null;

function resetStats() {
    ['stat-total', 'stat-top', 'stat-count', 'stat-avg'].forEach(id => {
        document.getElementById(id).textContent = '';
    });
}

function renderAnalytics(data) {
    const empty = document.getElementById('analytics-empty');
    const main  = document.getElementById('analytics-main');

    if (!data.length) {
        main.style.display = 'none';
        empty.textContent = 'No spending data for this period.';
        empty.style.display = 'block';
        resetStats();
        return;
    }

    empty.style.display = 'none';
    main.style.display = 'grid';

    const sorted = [...data].sort((a, b) => Number(b.totalSpent) - Number(a.totalSpent));
    const total  = sorted.reduce((s, d) => s + Number(d.totalSpent), 0);
    const avg    = total / sorted.length;

    document.getElementById('stat-total').textContent = fmt(total);
    document.getElementById('stat-top').textContent   = sorted[0].category;
    document.getElementById('stat-count').textContent = sorted.length;
    document.getElementById('stat-avg').textContent   = fmt(avg);

    if (chart) chart.destroy();
    chart = new Chart(document.getElementById('spending-chart'), {
        type: 'doughnut',
        data: {
            labels: sorted.map(d => d.category),
            datasets: [{
                data: sorted.map(d => Number(d.totalSpent).toFixed(2)),
                backgroundColor: COLORS.slice(0, sorted.length),
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => ` ${fmt(ctx.parsed)}`
                    }
                }
            }
        }
    });

    document.getElementById('analytics-body').innerHTML = sorted.map((d, i) => `
        <tr>
            <td>
                <span class="cat-dot" style="background:${COLORS[i] ?? '#ccc'}"></span>
                ${d.category}
            </td>
            <td class="num">${fmt(d.totalSpent)}</td>
            <td class="num">${((Number(d.totalSpent) / total) * 100).toFixed(1)}%</td>
        </tr>
    `).join('');
}

async function fetchAndRender() {
    const accountId = document.getElementById('account-picker').value;
    const month     = document.getElementById('month-picker').value;
    if (!accountId) return;

    try {
        const data = await API.analytics.getSpending(accountId, month);
        renderAnalytics(data);
    } catch (e) {
        document.getElementById('analytics-main').style.display = 'none';
        const empty = document.getElementById('analytics-empty');
        empty.textContent = 'Error: ' + e.message;
        empty.style.display = 'block';
        resetStats();
    }
}

async function load() {
    let customerId = params.get('customer');

    if (initAccountId && !customerId) {
        try {
            const acct = await API.accounts.getById(initAccountId);
            customerId = acct.customerId;
            document.getElementById('back-btn').href = `/pages/transactions.html?account=${initAccountId}`;
        } catch (e) {
            console.error(e);
        }
    } else if (customerId) {
        document.getElementById('back-btn').href = `/pages/dashboard.html?customer=${customerId}`;
    }

    if (!customerId) return;

    try {
        const accounts = await API.accounts.getByCustomer(customerId);
        const picker = document.getElementById('account-picker');
        picker.innerHTML = accounts.map(a => `
            <option value="${a.id}" ${a.id == initAccountId ? 'selected' : ''}>
                ${a.type.charAt(0).toUpperCase() + a.type.slice(1)} #A${String(a.id).padStart(3, '0')}
            </option>
        `).join('');

        const now = new Date();
        document.getElementById('month-picker').value =
            `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

        document.getElementById('account-picker').addEventListener('change', fetchAndRender);
        document.getElementById('month-picker').addEventListener('change', fetchAndRender);

        await fetchAndRender();
    } catch (e) {
        console.error(e);
    }
}

load();
