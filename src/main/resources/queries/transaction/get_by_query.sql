SELECT * FROM transaction 
WHERE merchantid IN (
    SELECT merchantid FROM merchant 
    WHERE name LIKE ?
);