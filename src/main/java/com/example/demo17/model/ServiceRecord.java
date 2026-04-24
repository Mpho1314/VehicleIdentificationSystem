package com.example.demo17.model;

import java.time.LocalDate;

public class ServiceRecord extends BaseRecord {
    private int    serviceId;
    private int    vehicleId;
    private String serviceType;
    private String description;
    private double cost;
    private String registrationNumber;

    public ServiceRecord() {}

    public ServiceRecord(int serviceId, int vehicleId, LocalDate date,
                         String serviceType, String description, double cost) {
        super(date);
        this.serviceId   = serviceId;
        this.vehicleId   = vehicleId;
        this.serviceType = serviceType;
        this.description = description;
        this.cost        = cost;
    }

    @Override
    public String getSummary() {
        return "Service [" + serviceType + "] — Cost: M" + cost;
    }

    public int    getServiceId()                     { return serviceId; }
    public void   setServiceId(int v)                { this.serviceId = v; }
    public int    getVehicleId()                     { return vehicleId; }
    public void   setVehicleId(int v)                { this.vehicleId = v; }
    public String getServiceType()                   { return serviceType; }
    public void   setServiceType(String v)           { this.serviceType = v; }
    public String getDescription()                   { return description; }
    public void   setDescription(String v)           { this.description = v; }
    public double getCost()                          { return cost; }
    public void   setCost(double v)                  { this.cost = v; }
    public String getRegistrationNumber()            { return registrationNumber; }
    public void   setRegistrationNumber(String v)    { this.registrationNumber = v; }
}
