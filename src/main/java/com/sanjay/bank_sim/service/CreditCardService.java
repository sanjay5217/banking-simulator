package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.AccountNotFoundException;
import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.model.Account;
import com.sanjay.bank_sim.model.CreditCard;
import com.sanjay.bank_sim.repository.AccountRepository;
import com.sanjay.bank_sim.repository.CreditCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final AccountRepository accountRepository;

    public CreditCardService(CreditCardRepository creditCardRepository, AccountRepository accountRepository) {
        this.creditCardRepository = creditCardRepository;
        this.accountRepository = accountRepository;
    }

    public List<CreditCard> getByCustomer(int customerId) {
        return this.creditCardRepository.findByCustomerId(customerId);
    }

    public CreditCard getById(int creditId) {
        return this.creditCardRepository.findById(creditId)
                .orElseThrow(() -> new AccountNotFoundException(creditId));
    }

    public void purchase(int creditId, BigDecimal amount) {
        int rows = creditCardRepository.purchase(creditId, amount);
        if (rows == 0) {
            throw new IllegalArgumentException("Purchase exceeds credit limit");
        }
    }

    public Map<String, BigDecimal> minimumPayment(int creditId) {
        CreditCard card = getById(creditId);
        BigDecimal balance = card.getBalance();
        BigDecimal min = balance.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.ZERO
                : balance.multiply(new BigDecimal("0.02"))
                         .max(new BigDecimal("10.00"))
                         .setScale(2, RoundingMode.HALF_UP);
        return Map.of("minimumPayment", min);
    }

    @Transactional
    public void payCard(int creditId, int fromAccountId, BigDecimal amount) {
        Account account = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromAccountId);
        }
        accountRepository.updateBalance(fromAccountId, amount.negate());
        accountRepository.updateTransaction(fromAccountId, amount.negate(), "Credit Card Payment");
        creditCardRepository.payCard(creditId, amount);
    }
}
