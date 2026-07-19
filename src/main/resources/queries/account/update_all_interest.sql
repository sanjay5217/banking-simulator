WITH information AS (
      SELECT
            account.accountid AS accountid,
            account.balance AS old_balance,
            (account.balance * specific.interest_rate / 12) AS interest
      FROM account
      JOIN savings_account AS specific
      /*
      JOIN (
            SELECT accountid, interest_rate
            FROM savings_account
            UNION ALL
            SELECT accountid, interest_rate
            FROM tfsa_account
            UNION ALL
            SELECT accountid, interest_rate
            FROM rrsp_account
      ) specific
      */
      ON account.accountid = specific.accountid
),

updates AS (
      UPDATE account
      SET balance = information.old_balance + information.interest
      FROM information
      WHERE information.accountid = account.accountid
)

INSERT INTO transaction (accountid, date, description, amount, merchantid)
SELECT information.accountid, CURRENT_DATE, 'Interest Applied', information.interest, NULL
FROM information;
