const AVATAR_COLORS = [
    '#0B2E59', '#3B6EA5', '#1E7A46', '#7c3aed',
    '#B3261E', '#0891b2', '#be185d', '#C9A227'
];

function initials(name) {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
}

function avatarColor(id) {
    return AVATAR_COLORS[id % AVATAR_COLORS.length];
}

let allCustomers = [];
let debounceTimer = null;

function renderGrid(customers) {
    const grid = document.getElementById('customer-grid');
    const empty = document.getElementById('no-results');

    if (!customers.length) {
        grid.innerHTML = '';
        empty.style.display = 'block';
        return;
    }

    empty.style.display = 'none';
    grid.innerHTML = customers.map(c => `
        <a class="customer-card" href="/pages/dashboard.html?customer=${c.id}">
            <div class="c-avatar" style="background:${avatarColor(c.id)}">${initials(c.name)}</div>
            <span class="c-name">${c.name}</span>
            <span class="c-email">${c.email}</span>
            <span class="c-since">Member since ${c.memberSince ?? ''}</span>
        </a>
    `).join('');
}

document.getElementById('search').addEventListener('input', e => {
    const q = e.target.value.trim();
    clearTimeout(debounceTimer);

    if (!q) {
        renderGrid(allCustomers);
        return;
    }

    debounceTimer = setTimeout(async () => {
        try {
            const results = await API.customers.searchByName(q);
            renderGrid(results);
        } catch (err) {
            console.error('Search failed', err);
        }
    }, 300);
});

async function load() {
    try {
        allCustomers = await API.customers.getAll();
        renderGrid(allCustomers);
    } catch (e) {
        console.error('Failed to load customers', e);
        document.getElementById('no-results').textContent = 'Error loading customers: ' + e.message;
        document.getElementById('no-results').style.display = 'block';
    }
}

load();
