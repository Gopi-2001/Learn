package com.airtribe.meditrack.exception;

public class AppointmentNotFoundException extends Exception {
    public AppointmentNotFoundException(String id) {
        super("Appointment not found: " + id);
    }
}
