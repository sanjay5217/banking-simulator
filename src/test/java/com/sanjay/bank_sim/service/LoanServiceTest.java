package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.model.FraudFlag;
import com.sanjay.bank_sim.model.Loan;
import com.sanjay.bank_sim.repository.CreditCardRepository;
import com.sanjay.bank_sim.repository.FraudRepository;
import com.sanjay.bank_sim.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock LoanRepository loanRepository;
    @Mock FraudRepository fraudRepository;
    @Mock CreditCardRepository creditCardRepository;
    @InjectMocks LoanService loanService;

    private Loan loan(String type) {
        return new Loan(1, 1, type, new BigDecimal("10000.00"),
                new BigDecimal("0.0899"), 36, LocalDate.of(2025, 1, 1));
    }

    private FraudFlag flag(boolean resolved) {
        return new FraudFlag(1, 1, "Large transaction", LocalDateTime.of(2025, 1, 1, 0, 0), resolved);
    }

    private CreditCard card(BigDecimal balance, BigDecimal limit) {
        return new CreditCard(1, 1, limit, balance, new BigDecimal("0.1999"));
    }

    @Test
    void createLoan_validPersonalLoan_returnsLoan() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of());
        when(fraudRepository.findByCustomerId(1)).thenReturn(List.of());
        when(creditCardRepository.findByCustomerId(1)).thenReturn(List.of());
        when(loanRepository.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(1);
        when(loanRepository.findById(1)).thenReturn(loan("personal"));

        Loan result = loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36);
        assertNotNull(result);
    }

    @Test
    void createLoan_invalidType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "creditcard", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_rateTooLow_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.005"), 36));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_rateTooHigh_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.31"), 36));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_amountBelowMinimum_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("500.00"), new BigDecimal("0.0899"), 36));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_amountAboveMaximum_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("60000.00"), new BigDecimal("0.0899"), 36));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_termBelowMinimum_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 6));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_termAboveMaximum_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 72));
        verifyNoInteractions(loanRepository, fraudRepository, creditCardRepository);
    }

    @Test
    void createLoan_duplicateLoanType_throwsIllegalArgument() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of(loan("personal")));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
    }

    @Test
    void createLoan_unresolvedFraudFlag_throwsIllegalArgument() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of());
        when(fraudRepository.findByCustomerId(1)).thenReturn(List.of(flag(false)));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
        verifyNoInteractions(creditCardRepository);
    }

    @Test
    void createLoan_resolvedFraudFlagOnly_doesNotThrow() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of());
        when(fraudRepository.findByCustomerId(1)).thenReturn(List.of(flag(true)));
        when(creditCardRepository.findByCustomerId(1)).thenReturn(List.of());
        when(loanRepository.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(1);
        when(loanRepository.findById(1)).thenReturn(loan("personal"));

        assertDoesNotThrow(() -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
    }

    @Test
    void createLoan_creditCardUtilizationAbove75Percent_throwsIllegalArgument() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of());
        when(fraudRepository.findByCustomerId(1)).thenReturn(List.of());
        when(creditCardRepository.findByCustomerId(1)).thenReturn(
                List.of(card(new BigDecimal("800.00"), new BigDecimal("1000.00"))));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
    }

    @Test
    void createLoan_creditCardUtilizationAt75Percent_doesNotThrow() {
        when(loanRepository.findByCustomerId(1)).thenReturn(List.of());
        when(fraudRepository.findByCustomerId(1)).thenReturn(List.of());
        when(creditCardRepository.findByCustomerId(1)).thenReturn(
                List.of(card(new BigDecimal("750.00"), new BigDecimal("1000.00"))));
        when(loanRepository.createLoan(anyInt(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(1);
        when(loanRepository.findById(1)).thenReturn(loan("personal"));

        assertDoesNotThrow(() -> loanService.createLoan(1, "personal", new BigDecimal("10000.00"), new BigDecimal("0.0899"), 36));
    }
}
