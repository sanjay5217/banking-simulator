SELECT
	account.balance - SUM(amopunt) AS opening_balance, 
	account.balance AS closing_balance,
	COUNT(amount) AS transactions,
	SUM(amount) AS total_amount,
	SUM(amount) FILTER (WHERE amount > 0) AS cash_in, 
	SUM(amount) FILTER (WHERE amount < 0) AS cash_out
FROM transaction 
WHERE accountid = ?;