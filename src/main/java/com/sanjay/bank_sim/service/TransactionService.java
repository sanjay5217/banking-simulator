package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.model.Transaction;
import com.sanjay.bank_sim.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getByAccountId(int accountId) {
        return this.transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> getByQuery(String query) {
       return this.transactionRepository.findByQuery(query);
    }
}
