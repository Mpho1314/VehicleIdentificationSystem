package com.example.demo17.model;

public class Vehicle {
    private int    vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private int    year;
    private String color;
    private int    ownerId;
    private String ownerName;

    public Vehicle() {}

    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, String color, int ownerId) {
        this.vehicleId          = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make               = make;
        this.model              = model;
        this.year               = year;
        this.color              = color;
        this.ownerId            = ownerId;
    }

    public int    getVehicleId()                        { return vehicleId; }
    public void   setVehicleId(int v)                   { this.vehicleId = v; }
    public String getRegistrationNumber()               { return registrationNumber; }
    public void   setRegistrationNumber(String v)       { this.registrationNumber = v; }
    public String getMake()                             { return make; }
    public void   setMake(String v)                     { this.make = v; }
    public String getModel()                            { return model; }
    public void   setModel(String v)                    { this.model = v; }
    public int    getYear()                             { return year; }
    public void   setYear(int v)                        { this.year = v; }
    public String getColor()                            { return color; }
    public void   setColor(String v)                    { this.color = v; }
    public int    getOwnerId()                          { return ownerId; }
    public void   setOwnerId(int v)                     { this.ownerId = v; }
    public String getOwnerName()                        { return ownerName; }
    public void   setOwnerName(String v)                { this.ownerName = v; }

    @Override
    public String toString() {
        return registrationNumber + " — " + make + " " + model + " (" + year + ")";
    }
}

