const params = new URLSearchParams(window.location.search);
const customerId = params.get('customer');

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

let loans = [];
let activeLoanId = null;

const LOAN_CONFIG = {
    personal: { rate: 8.99,  minAmount: 1000,   maxAmount: 50000,    minTerm: 12,  maxTerm: 60  },
    mortgage: { rate: 5.25,  minAmount: 50000,   maxAmount: 1000000,  minTerm: 60,  maxTerm: 360 },
    auto:     { rate: 6.49,  minAmount: 5000,    maxAmount: 100000,   minTerm: 12,  maxTerm: 84  },
    student:  { rate: 4.50,  minAmount: 1000,    maxAmount: 100000,   minTerm: 12,  maxTerm: 120 },
    business: { rate: 7.99,  minAmount: 10000,   maxAmount: 500000,   minTerm: 12,  maxTerm: 120 }
};

function renderTabs() {
    const container = document.getElementById('loan-tabs');
    container.innerHTML = loans.map(l => `
        <button class="loan-tab ${l.id == activeLoanId ? 'active' : ''}" data-id="${l.id}">
            ${l.type.charAt(0).toUpperCase() + l.type.slice(1)} #L${String(l.id).padStart(3, '0')}
        </button>
    `).join('');
    container.querySelectorAll('.loan-tab').forEach(btn => {
        btn.addEventListener('click', () => selectLoan(Number(btn.dataset.id)));
    });
}

async function selectLoan(id) {
    activeLoanId = id;
    renderTabs();

    const loan = loans.find(l => l.id === id);
    document.getElementById('loan-detail').style.display = 'block';
    document.getElementById('detail-principal').textContent = fmt(loan.principal);
    document.getElementById('detail-rate').textContent = (loan.interestRate * 100).toFixed(2) + '%';
    document.getElementById('detail-term').textContent = loan.termMonths + ' mo';
    document.getElementById('detail-start').textContent = loan.startDate;
    document.getElementById('detail-monthly').textContent = '...';

    try {
        const mp = await API.loans.getMonthlyPayment(id);
        document.getElementById('detail-monthly').textContent = fmt(mp.monthlyPayment);
    } catch { /* backend may not be ready */ }

    try {
        const schedule = await API.loans.getSchedule(id);
        renderSchedule(schedule, loan);
    } catch (e) {
        document.getElementById('schedule-body').innerHTML =
            `<tr class="empty-row"><td colspan="6">Error loading schedule: ${e.message}</td></tr>`;
    }
}

function renderSchedule(schedule, loan) {
    const today = new Date().toISOString().slice(0, 10);
    const paid = schedule.filter(p => p.paymentDate <= today).length;
    const total = loan.termMonths;
    const pct = total > 0 ? Math.round((paid / total) * 100) : 0;

    const progSection = document.getElementById('loan-progress-section');
    progSection.style.display = 'flex';
    document.getElementById('progress-pct').textContent = pct + '%';
    document.getElementById('progress-fill').style.width = pct + '%';
    document.getElementById('progress-label').textContent = `${paid} / ${total} payments made`;
    document.getElementById('progress-remaining').textContent = `${total - paid} remaining`;

    if (!schedule.length) {
        document.getElementById('schedule-body').innerHTML =
            '<tr class="empty-row"><td colspan="6">No payment records</td></tr>';
        return;
    }

    let balance = Number(loan.principal);
    document.getElementById('schedule-body').innerHTML = schedule.map((p, i) => {
        balance = Math.max(0, balance - Number(p.principalPortion));
        const isPaid = p.paymentDate <= today;
        return `
            <tr class="${isPaid ? 'paid-row' : 'future-row'}">
                <td>${i + 1}</td>
                <td>${p.paymentDate}</td>
                <td class="num">${fmt(p.amount)}</td>
                <td class="num">${fmt(p.principalPortion)}</td>
                <td class="num interest-cell">${fmt(p.interestPortion)}</td>
                <td class="num">${fmt(balance)}</td>
            </tr>`;
    }).join('');
}

function updateFormHints() {
    const type = document.getElementById('loan-type').value;
    const cfg = LOAN_CONFIG[type];
    document.getElementById('amount-hint').textContent =
        `$${cfg.minAmount.toLocaleString('en-CA')} to $${cfg.maxAmount.toLocaleString('en-CA')}`;
    document.getElementById('term-hint').textContent = `${cfg.minTerm} to ${cfg.maxTerm} mo`;
    document.getElementById('loan-rate').value = cfg.rate;
}

function openForm() {
    document.getElementById('loan-form-panel').style.display = 'block';
    document.getElementById('btn-take-loan').style.display = 'none';
    document.getElementById('loan-form-msg').textContent = '';
    document.getElementById('loan-form-msg').className = 'loan-form-msg';
    updateFormHints();
}

function closeForm() {
    document.getElementById('loan-form-panel').style.display = 'none';
    document.getElementById('btn-take-loan').style.display = '';
}

async function submitLoan() {
    const type = document.getElementById('loan-type').value;
    const principal = Number(document.getElementById('loan-principal').value);
    const ratePct = Number(document.getElementById('loan-rate').value);
    const term = Number(document.getElementById('loan-term').value);
    const msgEl = document.getElementById('loan-form-msg');

    if (!principal || !ratePct || !term) {
        msgEl.textContent = 'All fields are required.';
        msgEl.className = 'loan-form-msg error';
        return;
    }

    const applyBtn = document.getElementById('loan-apply-btn');
    applyBtn.disabled = true;
    applyBtn.textContent = 'Applying...';

    try {
        const loan = await API.loans.create(customerId, {
            type,
            principal,
            interestRate: ratePct / 100,
            termMonths: term
        });

        loans.push(loan);
        document.getElementById('no-loans').style.display = 'none';
        renderTabs();
        closeForm();
        await selectLoan(loan.id);
    } catch (e) {
        msgEl.textContent = e.message;
        msgEl.className = 'loan-form-msg error';
    } finally {
        applyBtn.disabled = false;
        applyBtn.textContent = 'Apply';
    }
}

async function load() {
    if (!customerId) return;
    document.getElementById('back-btn').href = `/pages/dashboard.html?customer=${customerId}`;
    document.getElementById('btn-take-loan').addEventListener('click', openForm);
    document.getElementById('loan-cancel-btn').addEventListener('click', closeForm);
    document.getElementById('loan-apply-btn').addEventListener('click', submitLoan);
    document.getElementById('loan-type').addEventListener('change', updateFormHints);

    try {
        loans = await API.loans.getByCustomer(customerId);
        if (!loans.length) {
            document.getElementById('no-loans').style.display = 'block';
            return;
        }
        renderTabs();
        const loanParam = params.get('loan');
        const startId = loanParam ? Number(loanParam) : loans[0].id;
        await selectLoan(startId);
    } catch (e) {
        console.error(e);
        const el = document.getElementById('no-loans');
        el.textContent = 'Error loading loans: ' + e.message;
        el.style.display = 'block';
    }
}

load();
