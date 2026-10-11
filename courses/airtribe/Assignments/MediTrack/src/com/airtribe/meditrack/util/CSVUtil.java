package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CSVUtil {
    
    private CSVUtil() { }

    public static void savePatients(String file, Iterable<Patient> patients) throws IOException {
        Path path = Path.of(file);
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(file))) {
            for (Patient patient : patients) writer.write(patient.getId() + "," + patient.getName() + "," + patient.getAge() + "," + patient.getPhone() + System.lineSeparator());
        }
    }
    
    public static List<Patient> loadPatients(String file) throws IOException, InvalidDataException {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) patients.add(new Patient(values[0], values[1], Integer.parseInt(values[2]), values[3]));
            }
        }
        return patients;
    }
}
