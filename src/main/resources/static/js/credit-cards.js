const params = new URLSearchParams(window.location.search);
const customerId = params.get('customer');

function fmt(n) {
    return '$' + Number(n).toLocaleString('en-CA', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function utilClass(pct) {
    if (pct < 30) return 'low';
    if (pct < 70) return 'medium';
    return 'high';
}

function renderCards(cards) {
    const grid = document.getElementById('cc-grid');

    if (!cards.length) {
        document.getElementById('no-cards').style.display = 'block';
        return;
    }

    grid.innerHTML = cards.map(c => {
        const cardId = c.creditid ?? c.id;
        const available = Number(c.creditLimit) - Number(c.balance);
        const pct = Math.min(100, Math.round((c.balance / c.creditLimit) * 100));
        const cls = utilClass(pct);
        return `
            <div class="cc-tile">
                <div class="cc-card-art">
                    <div class="cc-card-top">
                        <span class="cc-bank-name">Blue Sea Bank</span>
                        <span class="cc-id">#CC${String(cardId).padStart(3, '0')}</span>
                    </div>
                    <div class="cc-chip"></div>
                    <div class="cc-balance-wrap">
                        <div class="cc-balance-label">Current Balance</div>
                        <div class="cc-balance-display">${fmt(c.balance)}</div>
                    </div>
                </div>
                <div class="cc-body">
                    <div class="cc-stats">
                        <div class="cc-stat">
                            <span class="cc-stat-label">Credit Limit</span>
                            <span class="cc-stat-value">${fmt(c.creditLimit)}</span>
                        </div>
                        <div class="cc-stat">
                            <span class="cc-stat-label">Available</span>
                            <span class="cc-stat-value">${fmt(available)}</span>
                        </div>
                        <div class="cc-stat">
                            <span class="cc-stat-label">APR</span>
                            <span class="cc-stat-value">${(c.apr * 100).toFixed(2)}%</span>
                        </div>
                    </div>
                    <div class="cc-util">
                        <div class="cc-util-label">
                            <span>Utilization</span>
                            <span>${pct}%</span>
                        </div>
                        <div class="cc-util-track">
                            <div class="cc-util-fill ${cls}" style="width:${pct}%"></div>
                        </div>
                    </div>
                </div>
                <div class="cc-tile-footer">
                    <a href="/pages/credit-card-detail.html?card=${cardId}&customer=${customerId}" class="btn-view-sm">Manage →</a>
                </div>
            </div>`;
    }).join('');
}

async function load() {
    if (!customerId) return;
    document.getElementById('back-btn').href = `/pages/dashboard.html?customer=${customerId}`;

    try {
        const cards = await API.creditCards.getByCustomer(customerId);
        renderCards(cards);
    } catch (e) {
        console.error(e);
        const el = document.getElementById('no-cards');
        el.textContent = 'Error loading cards: ' + e.message;
        el.style.display = 'block';
    }
}

load();
