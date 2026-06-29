package com.sanjay.bank_sim.model;

import java.time.LocalDate;

public class Customer {
    private final int id;
    private final String name;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final LocalDate memberSince;

    public Customer(int id, String name, String email, String phone, LocalDate dateOfBirth, LocalDate memberSince) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.memberSince = memberSince;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalDate getMemberSince() { return memberSince; }
}
