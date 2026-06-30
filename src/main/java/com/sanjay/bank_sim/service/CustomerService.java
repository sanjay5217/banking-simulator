package com.sanjay.bank_sim.service;

import com.sanjay.bank_sim.exception.CustomerNotFoundException;
import com.sanjay.bank_sim.model.Customer;
import com.sanjay.bank_sim.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public List<Customer> searchByName(String name) {
        return customerRepository.searchByName(name);
    }
}
