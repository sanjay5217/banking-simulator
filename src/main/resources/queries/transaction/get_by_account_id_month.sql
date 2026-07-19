SELECT * FROM transaction
WHERE accountid = ?
AND TO_CHAR(date, 'YYYY-MM') = ?
ORDER BY transactionid;