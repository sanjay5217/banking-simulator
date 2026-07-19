SELECT ROUND(
    principal * (interest_rate/12) * POWER(1 + interest_rate/12, term_months)
    / (POWER(1 + interest_rate/12, term_months) - 1),
2) AS monthly_payment
FROM loan
WHERE loanid = ?;