SELECT creditid, customerid, credit_limit, balance, apr
FROM credit_card
WHERE customerid = ?
