UPDATE credit_card
SET balance = balance + ?
WHERE creditid = ? AND (balance + ?) <= credit_limit;
