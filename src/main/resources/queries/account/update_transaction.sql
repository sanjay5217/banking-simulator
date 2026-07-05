INSERT INTO transaction (accountid, date, description, amount, merchantid)
VALUES (?,CURRENT_DATE, ?, ?, NULL);