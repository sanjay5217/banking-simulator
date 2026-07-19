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
    if (!res.ok) {
        const text = await res.text();
        let message = `POST ${path} failed: ${res.status}`;
        try { const j = JSON.parse(text); if (j.error) message = j.error; } catch {}
        throw new Error(message);
    }
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
        getByAccountId: (accountId, month) => get(`/transaction/${accountId}${month ? '?month=' + month : ''}`),
        getByQuery: (query) => get(`/transaction/search/${query}`),
    },

    transfers: {
        internal: (body) => post(`/transfer/${body.toAccountId}`, body),
        external: (toAccountId, body) => post(`/transfer/${toAccountId}`, body),
        getHistory: (accountId) => get(`/transfer/summary/${accountId}`)
    },

    loans: {
        getByCustomer: (customerId) => get(`/loans/customer/${customerId}`),
        getById: (id) => get(`/loans/${id}`),
        getSchedule: (id) => get(`/loans/${id}/schedule`),
        getMonthlyPayment: (id) => get(`/loans/${id}/monthly-payment`),
        create: (customerId, body) => post(`/loans/customer/${customerId}`, body),
    },

    creditCards: {
        getByCustomer: (customerId) => get(`/credit-cards/customer/${customerId}`),
        getById: (id) => get(`/credit-cards/${id}`),
        purchase: (creditId, body) => post(`/credit-cards/${creditId}/purchase`, body),
        getMinimumPayment: (creditId) => get(`/credit-cards/${creditId}/minimum-payment`),
        pay: (creditId, body) => post(`/credit-cards/${creditId}/pay`, body)
    },

    merchants: {
        getAll: () => get('/merchants')
    },

    fraud: {
        getByCustomer: (customerId) => get(`/fraud/customer/${customerId}`),
        getUnresolved: () => get('/fraud/unresolved'),
        resolve: (flagId) => post(`/fraud/${flagId}/resolve`, {}),
        detect: (accountId) => post(`/fraud/detect/${accountId}`, {}),
    },

    analytics: {
        getSpending: (accountId, month) => {
            const q = month ? `?month=${month}` : '';
            return get(`/analytics/${accountId}${q}`);
        }
    },

    interest: {
        run: () => post('/interest-engine/run', {})
    }
};
