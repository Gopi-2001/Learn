package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import java.util.ArrayList;
import java.util.List;

public class PatientService {
    private final DataStore<Patient> patients = new DataStore<>();
    
    public void add(Patient patient) { 
        patients.save(patient); 
    }
    
    public Patient searchPatient(String id) { 
        return patients.findById(id); 
    
    }

    public List<Patient> searchPatientByName(String name) { 
        return filter(patient -> patient.matches(name)); 
    }

    public List<Patient> searchPatient(int age) { 
        return filter(patient -> patient.getAge() == age); 
    }
    
    public void updatePhone(String id, String phone) throws InvalidDataException { 
        Patient patient = searchPatient(id); 
        if (patient != null) 
            patient.setPhone(phone); 
    }

    public void delete(String id) { 
        patients.delete(id); 
    }

    public Iterable<Patient> getAll() { 
        return patients.findAll(); 
    }

    private List<Patient> filter(java.util.function.Predicate<Patient> condition) { 
        List<Patient> result = new ArrayList<>(); 
        for (Patient patient : patients.findAll()) 
            if (condition.test(patient)) 
                result.add(patient); 
        return result; 
    }
}
