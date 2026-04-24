package com.example.demo17.model;

import java.time.LocalDate;

public class Violation extends BaseRecord {
    private int    violationId;
    private int    vehicleId;
    private String violationType;
    private double fineAmount;
    private String status;
    private String registrationNumber;

    public Violation() {}

    public Violation(int violationId, int vehicleId, LocalDate date,
                     String violationType, double fineAmount, String status) {
        super(date);
        this.violationId   = violationId;
        this.vehicleId     = vehicleId;
        this.violationType = violationType;
        this.fineAmount    = fineAmount;
        this.status        = status;
    }

    @Override
    public String getSummary() {
        return "Violation: " + violationType + " | Fine: M" + fineAmount + " | " + status;
    }

    public int    getViolationId()                   { return violationId; }
    public void   setViolationId(int v)              { this.violationId = v; }
    public int    getVehicleId()                     { return vehicleId; }
    public void   setVehicleId(int v)                { this.vehicleId = v; }
    public String getViolationType()                 { return violationType; }
    public void   setViolationType(String v)         { this.violationType = v; }
    public double getFineAmount()                    { return fineAmount; }
    public void   setFineAmount(double v)            { this.fineAmount = v; }
    public String getStatus()                        { return status; }
    public void   setStatus(String v)                { this.status = v; }
    public String getRegistrationNumber()            { return registrationNumber; }
    public void   setRegistrationNumber(String v)    { this.registrationNumber = v; }
}

