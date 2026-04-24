package com.example.demo17.model;

public class Customer {
    private int    customerId;
    private String name;
    private String address;
    private String phone;
    private String email;

    public Customer() {}

    public Customer(int customerId, String name, String address, String phone, String email) {
        this.customerId = customerId;
        this.name       = name;
        this.address    = address;
        this.phone      = phone;
        this.email      = email;
    }

    public int    getCustomerId()      { return customerId; }
    public void   setCustomerId(int v) { this.customerId = v; }
    public String getName()            { return name; }
    public void   setName(String v)    { this.name = v; }
    public String getAddress()         { return address; }
    public void   setAddress(String v) { this.address = v; }
    public String getPhone()           { return phone; }
    public void   setPhone(String v)   { this.phone = v; }
    public String getEmail()           { return email; }
    public void   setEmail(String v)   { this.email = v; }

    @Override
    public String toString() { return name + " (" + email + ")"; }
}