package com.sanjay.bank_sim.model;

public class Merchant {
    private final int id;
    private final String name;
    private final String category;

    public Merchant(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
}
