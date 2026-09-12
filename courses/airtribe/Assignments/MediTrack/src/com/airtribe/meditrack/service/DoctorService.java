package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {
    private final DataStore<Doctor> doctors = new DataStore<>();
    
    public void add(Doctor doctor) { 
        doctors.save(doctor); 
    }

    public Doctor findById(String id) { 
        return doctors.findById(id); 
    }

    public List<Doctor> search(String name) { 
        List<Doctor> result = new ArrayList<>(); 
        for (Doctor doctor : doctors.findAll()) 
            if (doctor.getName().toLowerCase().contains(name.toLowerCase())) 
                result.add(doctor); 
        return result; 
    }

    public List<Doctor> search(Specialization specialization) { 
        List<Doctor> result = new ArrayList<>(); 
        for (Doctor doctor : doctors.findAll()) 
            if (doctor.getSpecialization() == specialization) 
                result.add(doctor); 
        return result; 
    }

    public void updatePhone(String id, String phone) throws InvalidDataException { 
        Doctor doctor = findById(id); 
        if (doctor != null) 
            doctor.setPhone(phone); 
    }

    public void delete(String id) { 
        doctors.delete(id); 
    }

    public Iterable<Doctor> getAll() { 
        return doctors.findAll(); 
    }
}
