package com.airtribe.learntrack.service;

import java.util.ArrayList;

import com.airtribe.learntrack.common.Status;
import com.airtribe.learntrack.entity.Enrollment;

public class EnrollmentService {
    private final ArrayList<Enrollment> enrollments = new ArrayList<>();

    public void enrollStudent(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void displayAllEnrollments() {
        for (Enrollment enrollment : enrollments) {
            enrollment.displayEnrollmentInfo();
            System.out.println("______________________________");
        }
    }

    public void viewEnrollmentByStudentId(int studentId) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudentId() == studentId) {
                enrollment.displayEnrollmentInfo();
		        System.out.println("______________________________");
            }
        }
    }

    public void updateEnrollmentStatus(int enrollmentId, String newStatus) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getId() == enrollmentId) {
                enrollment.setStatus(Status.valueOf(newStatus.toUpperCase()));
                System.out.println("Successfully updated the status of the enrollment with ID: " + enrollmentId);
                return;
            }
        }
        System.out.println("Failed to update the status. Enrollment with ID: " + enrollmentId + " not found.");
    }


}
