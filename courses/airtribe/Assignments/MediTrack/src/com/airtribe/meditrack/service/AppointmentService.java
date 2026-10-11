package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private final List<Appointment> appointments = new ArrayList<>();
    
    public synchronized void create(Appointment appointment) { 
        appointments.add(appointment); 
    }
    
    public synchronized List<Appointment> viewAll() { 
        return new ArrayList<>(appointments); 
    }
    
    public synchronized void cancel(String id) throws AppointmentNotFoundException { 
        for (Appointment appointment : appointments) 
            if (appointment.getId().equals(id)) { 
                appointment.cancel(); 
                return; 
            } 
        throw new AppointmentNotFoundException(id); 
    }
}
