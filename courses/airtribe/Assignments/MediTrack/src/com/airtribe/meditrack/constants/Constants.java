package com.airtribe.meditrack.constants;

public final class Constants {
    public static final double TAX_RATE = 0.05;
    public static final String PATIENT_CSV = "data/patients.csv";

    static {
        System.out.println("MediTrack configuration loaded.");
    }

    private Constants() {
    }
}
