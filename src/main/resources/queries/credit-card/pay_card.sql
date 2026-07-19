UPDATE credit_card
SET balance = GREATEST(0.00, balance - ?)
WHERE creditid = ?;
