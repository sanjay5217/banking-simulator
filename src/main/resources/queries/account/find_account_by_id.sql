SELECT * FROM account
    LEFT JOIN chequing_account AS ca ON ca.accountid = account.accountid
    LEFT JOIN savings_account AS sa ON sa.accountid = account.accountid
    LEFT JOIN tfsa_account AS ta ON ta.accountid = account.accountid
    LEFT JOIN rrsp_account AS ra ON ra.accountid = account.accountid
WHERE account.accountid = ?;