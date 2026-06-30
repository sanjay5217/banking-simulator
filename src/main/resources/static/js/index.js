async function loadCustomers() {
    const select = document.getElementById('customer-select');
    try {
        const customers = await API.customers.getAll();
        select.innerHTML = '<option value=""> choose a customer </option>';
        customers.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = `${c.name} — #C${String(c.id).padStart(3, '0')}`;
            select.appendChild(opt);
        });
    } catch (e) {
        console.error('Failed to load customers', e);
    }
}

function viewDashboard() {
    const select = document.getElementById('customer-select');
    if (!select.value) {
        select.focus();
        return;
    }
    window.location.href = `/dashboard.html?customer=${select.value}`;
}

loadCustomers();
