package com.airtribe.meditrack.test;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.PatientService;
import java.time.LocalDateTime;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        testPatientSearch();
        testDeepCopy();
        testBillTax();
        testCancellation();
        System.out.println("All manual tests passed.");
    }

    private static void testPatientSearch() throws InvalidDataException {
        PatientService service = new PatientService();
        service.add(new Patient("P9", "Ravi", 31, "555-9999"));
        check(service.searchPatient("P9") != null, "Patient ID search failed");
        check(service.searchPatientByName("rav").size() == 1, "Patient name search failed");
        check(service.searchPatient(31).size() == 1, "Patient age search failed");
    }

    private static void testDeepCopy() throws Exception {
        Doctor doctor = new Doctor("D9", "Dr Lee", 40, "555-0000", Specialization.GENERAL);
        Patient original = new Patient("P8", "Mina", 20, "555-1111"); original.setAssignedDoctor(doctor);
        Patient copy = original.clone(); copy.getAssignedDoctor().setName("Changed");
        check(!original.getAssignedDoctor().getName().equals("Changed"), "Patient clone was shallow");
    }

    private static void testBillTax() {
        Bill bill = new Bill("B1", "P1", 100, "Test");
        check(bill.getTotalAmount() == 105, "Tax calculation failed");
    }

    private static void testCancellation() throws Exception {
        Patient patient = new Patient("P7", "Sam", 25, "555-2222");
        Doctor doctor = new Doctor("D7", "Dr Kim", 50, "555-3333", Specialization.GENERAL);
        Appointment appointment = new Appointment("A7", patient, doctor, LocalDateTime.now());
        AppointmentService service = new AppointmentService();
        service.create(appointment);
        service.cancel("A7");
        check(appointment.getStatus() == AppointmentStatus.CANCELLED, "Cancellation failed");
    }

    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}