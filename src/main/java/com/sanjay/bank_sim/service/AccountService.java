package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccountsByCustomerId(int customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public Account getAccountById(int accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public void deposit(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Account account = getAccountById(accountId);
            accountRepository.updateBalance(accountId, account.getBalance().add(amount));
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    public void withdraw(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            Account account = getAccountById(accountId);
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountId);
            }
            accountRepository.updateBalance(accountId, account.getBalance().subtract(amount));
        } else {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
    }

    public Object getAccountSummary(int customerId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
