package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.model.FraudFlag;
import com.sanjay.bank_sim.repository.FraudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FraudService {

    private final FraudRepository fraudRepository;

    public FraudService(FraudRepository fraudRepository) {
        this.fraudRepository = fraudRepository;
    }

    public List<FraudFlag> getFlagsByCustomerId(int customerId) {
        return this.fraudRepository.findByCustomerId(customerId);
    }

    public List<FraudFlag> getUnresolvedFlags() {
        return this.fraudRepository.findUnresolved();
    }

    public void resolveFlag(int flagId) {
        this.fraudRepository.resolve(flagId);
    }

    public List<FraudFlag> detectAndFlagSuspicious(int accountId) {
        return this.fraudRepository.detectAndInsert(accountId);
    }
}
