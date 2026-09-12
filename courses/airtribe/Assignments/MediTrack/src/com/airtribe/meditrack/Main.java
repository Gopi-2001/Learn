package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    private static final PatientService PATIENTS = new PatientService();
    private static final DoctorService DOCTORS = new DoctorService();
    private static final AppointmentService APPOINTMENTS = new AppointmentService();

    public static void main(String[] args) {
        seedData();
        if (args.length > 0 && "--loadData".equals(args[0])) loadData();
        menu();
    }

    private static void seedData() {
        try {
            Doctor doctor = new Doctor("D1", "Dr Meera", 42, "555-1000", Specialization.CARDIOLOGY);
            Patient patient = new Patient("P1", "Asha", 28, "555-2000");
            patient.setAssignedDoctor(doctor);
            DOCTORS.add(doctor); PATIENTS.add(patient);
        } catch (InvalidDataException exception) { System.out.println(exception.getMessage()); }
    }

    private static void menu() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1 List patients  2 Add patient  3 Create appointment  4 List appointments");
                System.out.println("5 Bill  6 Search patient  7 Cancel appointment  8 Save patients  0 Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();
                if ("0".equals(choice)) return;
                if ("1".equals(choice)) for (Patient patient : PATIENTS.getAll()) System.out.println(patient.getDisplayName());
                if ("2".equals(choice)) addPatient(scanner);
                if ("3".equals(choice)) createAppointment(scanner);
                if ("4".equals(choice)) for (Appointment appointment : APPOINTMENTS.viewAll()) System.out.println(appointment.getDisplayName() + " - " + appointment.getStatus());
                if ("5".equals(choice)) createBill(scanner);
                if ("6".equals(choice)) searchPatient(scanner);
                if ("7".equals(choice)) cancelAppointment(scanner);
                if ("8".equals(choice)) saveData();
            }
        }
    }

    private static void addPatient(Scanner scanner) {
        try {
            System.out.print("Name: "); String name = scanner.nextLine();
            System.out.print("Age: "); int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Phone: "); String phone = scanner.nextLine();
            PATIENTS.add(new Patient(IdGenerator.getInstance().next("P"), name, age, phone));
        } catch (NumberFormatException | InvalidDataException exception) { System.out.println("Could not add patient: " + exception.getMessage()); }
    }

    private static void createAppointment(Scanner scanner) {
        System.out.print("Patient ID: "); Patient patient = PATIENTS.searchPatient(scanner.nextLine());
        System.out.print("Doctor ID: "); Doctor doctor = DOCTORS.findById(scanner.nextLine());
        if (patient == null || doctor == null) { System.out.println("Patient or doctor was not found."); return; }
        APPOINTMENTS.create(new Appointment(IdGenerator.getInstance().next("A"), patient, doctor, LocalDateTime.now().plusDays(1)));
    }

    private static void createBill(Scanner scanner) {
        System.out.print("Patient ID: "); Patient patient = PATIENTS.searchPatient(scanner.nextLine());
        if (patient == null) { System.out.println("Patient was not found."); return; }
        MedicalEntity entity = patient;
        Bill bill = entity.generateBill(500);
        System.out.println("Total with tax: " + bill.getTotalAmount());
    }

    private static void searchPatient(Scanner scanner) {
        System.out.print("Name contains: ");
        for (Patient patient : PATIENTS.searchPatientByName(scanner.nextLine())) System.out.println(patient.getDisplayName());
    }

    private static void cancelAppointment(Scanner scanner) {
        try {
            System.out.print("Appointment ID: ");
            APPOINTMENTS.cancel(scanner.nextLine());
            System.out.println("Appointment cancelled.");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void saveData() {
        try {
            CSVUtil.savePatients(Constants.PATIENT_CSV, PATIENTS.getAll());
            System.out.println("Patients saved to " + Constants.PATIENT_CSV);
        } catch (Exception exception) {
            System.out.println("Could not save CSV: " + exception.getMessage());
        }
    }

    private static void loadData() {
        if (!Files.exists(Path.of(Constants.PATIENT_CSV))) { System.out.println("No CSV found at " + Constants.PATIENT_CSV); return; }
        try { for (Patient patient : CSVUtil.loadPatients(Constants.PATIENT_CSV)) PATIENTS.add(patient); }
        catch (Exception exception) { System.out.println("Could not load CSV: " + exception.getMessage()); }
    }
}
