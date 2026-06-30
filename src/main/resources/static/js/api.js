const BASE = '/api';

async function get(path) {
    const res = await fetch(`${BASE}${path}`);
    if (!res.ok) throw new Error(`GET ${path} failed: ${res.status}`);
    return res.json();
}

async function post(path, body) {
    const res = await fetch(`${BASE}${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error(`POST ${path} failed: ${res.status}`);
    return res.json();
}

const API = {
    customers: {
        getAll: () => get('/customers'),
        getById:(id) => get(`/customers/${id}`),
        searchByName: (name) => get(`/customers/search?name=${encodeURIComponent(name)}`)
    },

    accounts: {
        getByCustomer: (customerId) => get(`/accounts/customer/${customerId}`),
        getById: (id) => get(`/accounts/${id}`), 
        // getByAccount: (customerId) => get(`/accounts/${customerId}/summary`), 
        deposit: (id) => post(`/accounts/${id}/deposit`), 
        withdraw: (id) => post(`/accounts/${id}/withdraw`)
    },

    transactions: {
        getByAccountId: (accountId) => get(`/transaction/${accountId}`),
        getByQuery: (query) => get(`/transaction/search/${query}`),
    }
};
