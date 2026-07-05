SELECT
	account.balance - SUM(amount) AS opening_balance,
	account.balance AS closing_balance,
	COUNT(amount) AS transactions,
	SUM(amount) AS total_amount,
	SUM(amount) FILTER (WHERE amount > 0) AS cash_in, 
	SUM(amount) FILTER (WHERE amount < 0) AS cash_out
FROM transaction 
JOIN account ON account.accountid = transaction.accountid
WHERE transaction.accountid = ?
GROUP BY account.balance;