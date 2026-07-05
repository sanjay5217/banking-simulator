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
    const text = await res.text();
    return text ? JSON.parse(text) : null;
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
        getSummary: (accountId) => get(`/accounts/${accountId}/summary`),
        deposit: (id, amount) => post(`/accounts/${id}/deposit`, { amount }),
        withdraw: (id, amount) => post(`/accounts/${id}/withdraw`, { amount }),
    },

    transactions: {
        getByAccountId: (accountId) => get(`/transaction/${accountId}`),
        getByQuery: (query) => get(`/transaction/search/${query}`),
    },

    transfers: {
        internal: (body) => post(`/transfer/${body.toAccountId}`, body),
        external: (toAccountId, body) => post(`/transfer/${toAccountId}`, body),
        getHistory: (accountId) => get(`/transfer/summary/${accountId}`)
    }
};
