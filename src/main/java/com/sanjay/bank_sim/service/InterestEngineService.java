package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.repository.AccountRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InterestEngineService {

    private final AccountRepository accountRepository;

    public InterestEngineService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void updateInterest() {
        this.accountRepository.updateAllInterest();
    }
}
