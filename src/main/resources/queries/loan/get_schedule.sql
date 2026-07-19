WITH RECURSIVE schedule AS (
    SELECT
        1 AS payment_num,
        principal::NUMERIC AS remaining_balance,
        ROUND(principal * (interest_rate/12) * POWER(1 + interest_rate/12, term_months) / (POWER(1 + interest_rate/12, term_months) - 1), 2) AS monthly_payment,
        interest_rate, 
        term_months,
        start_date
    FROM loan
    WHERE loanid = ?

    UNION ALL 

    SELECT
        payment_num + 1,
        remaining_balance - (monthly_payment - ROUND(remaining_balance * interest_rate/12, 2)),
        monthly_payment,
        interest_rate, 
        term_months, 
        start_date
    FROM schedule 
    WHERE payment_num < term_months
)

SElECT
    payment_num, 
    monthly_payment AS amount, 
    ROUND(remaining_balance * interest_rate / 12, 2) AS interest_portion,
    monthly_payment - ROUND(remaining_balance * interest_rate / 12, 2) AS principal_portion,
    remaining_balance,
    (start_date + (payment_num || ' months')::interval)::date AS payment_date
FROM schedule;
