INSERT INTO fraud_flag (transactionid, reason, flagged_date, resolved)
SELECT transaction.transactionid, 'Suspicious Transaction', CURRENT_DATE, false
FROM transaction
WHERE transaction.accountid = ?
    AND transaction.amount > (
        SELECT AVG(amount) + 3 * STDDEV(amount)
        FROM transaction
        WHERE accountid = ?
    )
    AND transaction.transactionid NOT IN (SELECT transactionid FROM fraud_flag)
RETURNING fraud_flagid, transactionid, reason, flagged_date, resolved;