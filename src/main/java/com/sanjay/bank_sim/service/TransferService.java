package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.InsufficientFundsException;
import com.sanjay.bank_sim.exception.TransferException;
import com.sanjay.bank_sim.model.Transfer;
import com.sanjay.bank_sim.repository.AccountRepository;
import com.sanjay.bank_sim.utils.SqlLoader;
import com.sanjay.bank_sim.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        this.transferRepository.transferAmount(fromAccountId, toAccountId, amount);
        this.accountRepository.updateBalance(fromAccountId, amount.negate());
        this.accountRepository.updateTransaction(fromAccountId, amount.negate(), "Transfer");
        this.accountRepository.updateBalance(toAccountId, amount);
        this.accountRepository.updateTransaction(toAccountId, amount, "Transfer");
    }

    public List<Transfer> getTransferHistory(int accountId) {
        return this.transferRepository.getHistory(accountId);
    }

}
