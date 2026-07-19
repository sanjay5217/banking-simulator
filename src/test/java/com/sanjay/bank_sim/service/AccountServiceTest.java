package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @InjectMocks AccountService accountService;

    private Account accountWith(BigDecimal balance) {
        return new Account(1, 1, "chequing", balance);
    }

    @Test
    void deposit_positiveAmount_updatesBalanceAndLogsTransaction() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(accountWith(new BigDecimal("500.00"))));

        accountService.deposit(1, new BigDecimal("100.00"));

        verify(accountRepository).updateBalance(1, new BigDecimal("100.00"));
        verify(accountRepository).updateTransaction(1, new BigDecimal("100.00"), "Deposit");
    }

    @Test
    void deposit_zeroAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(1, BigDecimal.ZERO));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void deposit_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(1, new BigDecimal("-50.00")));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void withdraw_sufficientFunds_updatesBalanceAndLogsTransaction() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(accountWith(new BigDecimal("500.00"))));

        accountService.withdraw(1, new BigDecimal("200.00"));

        verify(accountRepository).updateBalance(1, new BigDecimal("-200.00"));
        verify(accountRepository).updateTransaction(1, new BigDecimal("-200.00"), "Withdraw");
    }

    @Test
    void withdraw_exactBalance_succeeds() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(accountWith(new BigDecimal("100.00"))));

        accountService.withdraw(1, new BigDecimal("100.00"));

        verify(accountRepository).updateBalance(1, new BigDecimal("-100.00"));
    }

    @Test
    void withdraw_insufficientFunds_throwsAndDoesNotUpdateBalance() {
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(accountWith(new BigDecimal("100.00"))));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(1, new BigDecimal("500.00")));

        verify(accountRepository, never()).updateBalance(anyInt(), any());
        verify(accountRepository, never()).updateTransaction(anyInt(), any(), any());
    }

    @Test
    void withdraw_zeroAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(1, BigDecimal.ZERO));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void withdraw_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(1, new BigDecimal("-10.00")));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void getAccountById_missing_throwsAccountNotFoundException() {
        when(accountRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById(99));
    }
}
