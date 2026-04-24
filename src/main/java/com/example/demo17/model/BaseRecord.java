package com.example.demo17.model;

import java.time.LocalDate;

public abstract class BaseRecord {
    protected LocalDate recordDate;

    public BaseRecord() {}
    public BaseRecord(LocalDate recordDate) { this.recordDate = recordDate; }

    public LocalDate getRecordDate()            { return recordDate; }
    public void      setRecordDate(LocalDate v) { this.recordDate = v; }

    public abstract String getSummary();
}

