package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.repository.AccountRepository;
import com.sanjay.bank_sim.repository.CreditCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

    @Mock CreditCardRepository creditCardRepository;
    @Mock AccountRepository accountRepository;
    @InjectMocks CreditCardService creditCardService;

    private CreditCard card(BigDecimal balance, BigDecimal limit) {
        return new CreditCard(1, 1, limit, balance, new BigDecimal("0.1999"));
    }

    private Account account(BigDecimal balance) {
        return new Account(1, 1, "chequing", balance);
    }

    @Test
    void getById_found_returnsCard() {
        when(creditCardRepository.findById(1))
                .thenReturn(Optional.of(card(new BigDecimal("200.00"), new BigDecimal("1000.00"))));

        assertNotNull(creditCardService.getById(1));
    }

    @Test
    void getById_missing_throwsAccountNotFoundException() {
        when(creditCardRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> creditCardService.getById(99));
    }

    @Test
    void purchase_withinLimit_succeeds() {
        when(creditCardRepository.purchase(1, new BigDecimal("100.00"))).thenReturn(1);

        assertDoesNotThrow(() -> creditCardService.purchase(1, new BigDecimal("100.00")));
    }

    @Test
    void purchase_exceedsLimit_throwsIllegalArgument() {
        when(creditCardRepository.purchase(1, new BigDecimal("2000.00"))).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> creditCardService.purchase(1, new BigDecimal("2000.00")));
    }

    @Test
    void minimumPayment_balanceRequires2Percent_returns2Percent() {
        when(creditCardRepository.findById(1))
                .thenReturn(Optional.of(card(new BigDecimal("1000.00"), new BigDecimal("2000.00"))));

        Map<String, BigDecimal> result = creditCardService.minimumPayment(1);
        assertEquals(0, result.get("minimumPayment").compareTo(new BigDecimal("20.00")));
    }

    @Test
    void minimumPayment_balanceTooLowFor2Percent_returnsFloor10() {
        when(creditCardRepository.findById(1))
                .thenReturn(Optional.of(card(new BigDecimal("200.00"), new BigDecimal("1000.00"))));

        Map<String, BigDecimal> result = creditCardService.minimumPayment(1);
        assertEquals(0, result.get("minimumPayment").compareTo(new BigDecimal("10.00")));
    }

    @Test
    void minimumPayment_zeroBalance_returnsZero() {
        when(creditCardRepository.findById(1))
                .thenReturn(Optional.of(card(BigDecimal.ZERO, new BigDecimal("1000.00"))));

        Map<String, BigDecimal> result = creditCardService.minimumPayment(1);
        assertEquals(0, result.get("minimumPayment").compareTo(BigDecimal.ZERO));
    }

    @Test
    void payCard_sufficientFunds_updatesAccountAndCard() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(account(new BigDecimal("500.00"))));

        creditCardService.payCard(1, 1, new BigDecimal("100.00"));

        verify(accountRepository).updateBalance(1, new BigDecimal("-100.00"));
        verify(accountRepository).updateTransaction(1, new BigDecimal("-100.00"), "Credit Card Payment");
        verify(creditCardRepository).payCard(1, new BigDecimal("100.00"));
    }

    @Test
    void payCard_accountNotFound_throwsAccountNotFoundException() {
        when(accountRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> creditCardService.payCard(1, 99, new BigDecimal("100.00")));
        verifyNoInteractions(creditCardRepository);
    }

    @Test
    void payCard_insufficientFunds_throwsAndDoesNotUpdate() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(account(new BigDecimal("50.00"))));

        assertThrows(InsufficientFundsException.class, () -> creditCardService.payCard(1, 1, new BigDecimal("100.00")));
        verify(accountRepository, never()).updateBalance(anyInt(), any());
        verify(creditCardRepository, never()).payCard(anyInt(), any());
    }
}
