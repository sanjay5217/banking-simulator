SELECT * FROM fraud_flag
JOIN transaction ON fraud_flag.transactionid = transaction.transactionid
JOIN account ON transaction.accountid = account.accountid
WHERE account.customerid = ?;