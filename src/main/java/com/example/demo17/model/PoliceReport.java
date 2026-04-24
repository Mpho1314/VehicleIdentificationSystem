package com.example.demo17.model;

import java.time.LocalDate;

public class PoliceReport extends BaseRecord {
    private int    reportId;
    private int    vehicleId;
    private String reportType;
    private String description;
    private String officerName;
    private String registrationNumber;

    public PoliceReport() {}

    public PoliceReport(int reportId, int vehicleId, LocalDate date,
                        String reportType, String description, String officerName) {
        super(date);
        this.reportId    = reportId;
        this.vehicleId   = vehicleId;
        this.reportType  = reportType;
        this.description = description;
        this.officerName = officerName;
    }

    @Override
    public String getSummary() {
        return "Report [" + reportType + "] by " + officerName + " on " + recordDate;
    }

    public int    getReportId()                      { return reportId; }
    public void   setReportId(int v)                 { this.reportId = v; }
    public int    getVehicleId()                     { return vehicleId; }
    public void   setVehicleId(int v)                { this.vehicleId = v; }
    public String getReportType()                    { return reportType; }
    public void   setReportType(String v)            { this.reportType = v; }
    public String getDescription()                   { return description; }
    public void   setDescription(String v)           { this.description = v; }
    public String getOfficerName()                   { return officerName; }
    public void   setOfficerName(String v)           { this.officerName = v; }
    public String getRegistrationNumber()            { return registrationNumber; }
    public void   setRegistrationNumber(String v)    { this.registrationNumber = v; }
}
