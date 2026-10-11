package com.airtribe.meditrack.entity;

import java.io.Serializable;

public abstract class MedicalEntity implements Serializable {
    private final String id;

    protected MedicalEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String getDisplayName();

    public abstract Bill generateBill(double amount);
}
