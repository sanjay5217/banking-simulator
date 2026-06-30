CREATE TABLE customers (
	customerid SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	phone VARCHAR(20) NOT NULL,
	date_of_birth DATE,
	member_since DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE account (
	accountid SERIAL PRIMARY KEY,
	customerid INT NOT NULL REFERENCES customers(customerid),
	type VARCHAR(20) NOT NULL CHECK (type IN ('chequing', 'savings')),
    balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE chequing_account (
    accountid INT PRIMARY KEY REFERENCES account(accountid),
    overdraft_limit NUMERIC(12, 2) DEFAULT 0.00,
    free_transaction_limit INT NOT NULL DEFAULT 0,
    monthly_fee NUMERIC(10, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE savings_account (
    accountid INT PRIMARY KEY REFERENCES account(accountid),
    interest_rate NUMERIC(6, 4) NOT NULL DEFAULT 0.00,
    min_balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE merchant (
    merchantid SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL
);

CREATE TABLE credit_card (
    creditid SERIAL PRIMARY KEY,
    customerid INT NOT NULL REFERENCES customers(customerid),
    credit_limit NUMERIC(12, 2) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    apr NUMERIC(6, 4) NOT NULL
);

CREATE TABLE loan (
    loanid SERIAL PRIMARY KEY,
    customerid INT NOT NULL REFERENCES customers(customerid),
    type VARCHAR(20) NOT NULL,
    principal NUMERIC(15, 2) NOT NULL,
    interest_rate NUMERIC(6, 4) NOT NULL,
    term_months INT NOT NULL,
    start_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE loan_payment (
    loan_paymentid SERIAL PRIMARY KEY,
    loanid INT NOT NULL REFERENCES loan(loanid),
    payment_date DATE NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    principal_portion NUMERIC(12, 2) NOT NULL,
    interest_portion NUMERIC(12, 2) NOT NULL
);

CREATE TABLE transfer (
    transferid SERIAL PRIMARY KEY,
    from_accountid INT NOT NULL REFERENCES account(accountid),
    to_accountid INT NOT NULL REFERENCES account(accountid),
    amount NUMERIC(12, 2) NOT NULL,
    date DATE NOT NULL
);

CREATE TABLE transaction (
    transactionid SERIAL PRIMARY KEY,
    accountid INT NOT NULL REFERENCES account(accountid),
    date DATE NOT NULL,
    description VARCHAR(255),
    amount NUMERIC(12, 2) NOT NULL,
    merchantid INT REFERENCES merchant(merchantid)
);

CREATE TABLE fraud_flag (
    fraud_flagid SERIAL PRIMARY KEY,
    transactionid INT NOT NULL REFERENCES transaction(transactionid),
    reason TEXT NOT NULL,
    flagged_date DATE NOT NULL,
    resolved BOOL NOT NULL DEFAULT false
);
