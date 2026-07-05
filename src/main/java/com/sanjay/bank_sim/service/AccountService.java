package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccountsByCustomerId(int customerId) {
        return this.accountRepository.findByCustomerId(customerId);
    }

    public Account getAccountById(int accountId) {
        return this.accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public void deposit(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Account account = getAccountById(accountId);
            this.accountRepository.updateBalance(accountId, amount);
            this.accountRepository.updateTransaction(accountId, amount, "Deposit");
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    @Transactional
    public void withdraw(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Account account = getAccountById(accountId);
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountId);
            }
            this.accountRepository.updateBalance(accountId, amount.negate());
            this.accountRepository.updateTransaction(accountId, amount.negate(), "Withdraw");
        } else {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
    }
}
