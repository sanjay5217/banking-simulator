const params = new URLSearchParams(window.location.search);
const customerId = params.get('customer');

function renderFlags(flags) {
    const el = document.getElementById('flags-list');

    if (!flags.length) {
        el.innerHTML = '<p class="empty-fraud">No fraud flags on this account.</p>';
        return;
    }

    el.innerHTML = flags.map(f => `
        <div class="flag-row ${f.resolved ? 'resolved' : ''}" id="flag-${f.id}">
            <div class="flag-left">
                <span class="flag-reason">${f.reason}</span>
                <span class="flag-meta">Transaction #${f.transactionId} &middot; ${f.flaggedAt?.slice(0, 10) ?? ''}</span>
            </div>
            <div style="display:flex;align-items:center;gap:10px">
                <span class="flag-badge ${f.resolved ? 'resolved' : 'open'}">${f.resolved ? 'Resolved' : 'Open'}</span>
                ${!f.resolved ? `<button class="btn-resolve" onclick="resolve(${f.id})">Resolve</button>` : ''}
            </div>
        </div>
    `).join('');
}

async function resolve(flagId) {
    try {
        await API.fraud.resolve(flagId);
        const row = document.getElementById(`flag-${flagId}`);
        row.classList.add('resolved');
        row.querySelector('.flag-badge').className = 'flag-badge resolved';
        row.querySelector('.flag-badge').textContent = 'Resolved';
        row.querySelector('.btn-resolve')?.remove();
    } catch (e) {
        console.error('Resolve failed', e);
    }
}

async function load() {
    if (!customerId) return;
    document.getElementById('back-btn').href = `/pages/dashboard.html?customer=${customerId}`;

    try {
        const flags = await API.fraud.getByCustomer(customerId);
        renderFlags(flags);
    } catch (e) {
        console.error(e);
        document.getElementById('flags-list').innerHTML =
            '<p class="empty-fraud">Error loading flags: ' + e.message + '</p>';
    }

    const accounts = await API.accounts.getByCustomer(customerId);

    document.getElementById('detect-btn').addEventListener('click', async () => {
        const btn = document.getElementById('detect-btn');
        const status = document.getElementById('detect-status');
        btn.disabled = true;
        btn.textContent = 'Running...';

        try {
            let newFlags = [];
            for (const a of accounts) {
                const result = await API.fraud.detect(a.id);
                newFlags = newFlags.concat(result);
            }

            status.style.display = 'block';
            if (newFlags.length) {
                status.className = 'detect-status';
                status.textContent = `${newFlags.length} new suspicious transaction${newFlags.length > 1 ? 's' : ''} flagged.`;
                const allFlags = await API.fraud.getByCustomer(customerId);
                renderFlags(allFlags);
            } else {
                status.className = 'detect-status none';
                status.textContent = 'No new suspicious transactions found.';
            }
        } catch (e) {
            console.error(e);
            status.style.display = 'block';
            status.className = 'detect-status none';
            status.textContent = 'Detection failed: ' + e.message;
        }

        btn.disabled = false;
        btn.textContent = 'Run Detection';
    });
}

load();
