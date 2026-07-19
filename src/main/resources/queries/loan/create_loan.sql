INSERT INTO loan (customerid, type, principal, interest_rate, term_months, start_date)
VALUES (?, ?, ?, ?, ?, CURRENT_DATE)
RETURNING loanid;
