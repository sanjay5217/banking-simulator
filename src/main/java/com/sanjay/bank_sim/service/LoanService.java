package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.model.Loan;
import com.sanjay.bank_sim.model.SchedulePayment;
import com.sanjay.bank_sim.repository.CreditCardRepository;
import com.sanjay.bank_sim.repository.FraudRepository;
import com.sanjay.bank_sim.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final FraudRepository fraudRepository;
    private final CreditCardRepository creditCardRepository;

    public LoanService(LoanRepository loanRepository, FraudRepository fraudRepository,
                       CreditCardRepository creditCardRepository) {
        this.loanRepository = loanRepository;
        this.fraudRepository = fraudRepository;
        this.creditCardRepository = creditCardRepository;
    }

    private static final Set<String> VALID_TYPES =
            Set.of("personal", "mortgage", "auto", "student", "business");

    private static final Map<String, BigDecimal[]> AMOUNT_LIMITS = Map.of(
            "personal", new BigDecimal[]{new BigDecimal("1000"),  new BigDecimal("50000")},
            "mortgage", new BigDecimal[]{new BigDecimal("50000"), new BigDecimal("1000000")},
            "auto",new BigDecimal[]{new BigDecimal("5000"),  new BigDecimal("100000")},
            "student", new BigDecimal[]{new BigDecimal("1000"),  new BigDecimal("100000")},
            "business", new BigDecimal[]{new BigDecimal("10000"), new BigDecimal("500000")}
    );

    private static final Map<String, int[]> TERM_LIMITS = Map.of(
            "personal", new int[]{12, 60},
            "mortgage", new int[]{60, 360},
            "auto", new int[]{12, 84},
            "student", new int[]{12, 120},
            "business", new int[]{12, 120}
    );

    public Loan getLoanById(int loanId) {
        return loanRepository.findById(loanId);
    }

    public List<Loan> getLoansByCustomerId(int customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<SchedulePayment> getAmortizationSchedule(int loanId) {
        return loanRepository.getLoanSchedule(loanId);
    }

    public BigDecimal calculateMonthlyPayment(int loanId) {
        Loan loan = loanRepository.findById(loanId);
        BigDecimal r = loan.getInterestRate().divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal pow = onePlusR.pow(loan.getTermMonths());
        return loan.getPrincipal().multiply(r).multiply(pow)
                   .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    public Loan createLoan(int customerId, String type, BigDecimal principal,
                            BigDecimal interestRate, int termMonths) {
        String lowerType = type.toLowerCase();

        if (!VALID_TYPES.contains(lowerType)) {
            throw new IllegalArgumentException("Invalid loan type: " + type);
        }

        if (interestRate.compareTo(new BigDecimal("0.01")) < 0
                || interestRate.compareTo(new BigDecimal("0.30")) > 0) {
            throw new IllegalArgumentException("Interest rate must be between 1% and 30%");
        }

        BigDecimal[] amounts = AMOUNT_LIMITS.get(lowerType);
        if (principal.compareTo(amounts[0]) < 0 || principal.compareTo(amounts[1]) > 0) {
            throw new IllegalArgumentException(cap(lowerType) + " loan principal must be between $"
                    + amounts[0].toPlainString() + " and $" + amounts[1].toPlainString());
        }

        int[] terms = TERM_LIMITS.get(lowerType);
        if (termMonths < terms[0] || termMonths > terms[1]) {
            throw new IllegalArgumentException(cap(lowerType) + " loan term must be between "
                    + terms[0] + " and " + terms[1] + " months");
        }

        boolean hasDuplicate = this.loanRepository.findByCustomerId(customerId).stream().anyMatch(l -> l.getType().equalsIgnoreCase(lowerType));
        if (hasDuplicate) {
            throw new IllegalArgumentException("Customer already has an active " + lowerType + " loan");
        }

        boolean hasFraud = this.fraudRepository.findByCustomerId(customerId).stream().anyMatch(f -> !f.isResolved());
        if (hasFraud) {
            throw new IllegalArgumentException("Loan denied. Unresolved fraud flags on account");
        }

        boolean highUtilization = creditCardRepository.findByCustomerId(customerId).stream().anyMatch(c -> {
            if (c.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) return false;
            return c.getBalance().divide(c.getCreditLimit(), 4, RoundingMode.HALF_UP)
                     .compareTo(new BigDecimal("0.75")) > 0;
        });
        if (highUtilization) {
            throw new IllegalArgumentException("Loan denied. Credit card utilization exceeds 75%");
        }

        int id = loanRepository.createLoan(customerId, lowerType, principal, interestRate, termMonths);
        return loanRepository.findById(id);
    }

    private String cap(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
