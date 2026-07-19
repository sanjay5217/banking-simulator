package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.model.Merchant;
import com.sanjay.bank_sim.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<Merchant> getAll() {
        return merchantRepository.findAll();
    }
}
