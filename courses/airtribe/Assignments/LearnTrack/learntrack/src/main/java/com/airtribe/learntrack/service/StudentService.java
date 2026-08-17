package com.airtribe.learntrack.service;

import java.util.ArrayList;
import java.util.List;

import com.airtribe.learntrack.entity.Student;
import com.airtribe.learntrack.exception.EntityNotFoundException;

public class StudentService {

    private final ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public Student getStudentById(int studentId) {
        for (Student student : students) {
            if (student.getId() == studentId) {
                return student;
            }
        }
        throw new EntityNotFoundException("StudentID: " + studentId);
    }

    public void removeStudent(int studentId) {
        try {
            Student student = getStudentById(studentId);
            students.remove(student);
            System.out.println("Successfully removed the student with ID: " + studentId);
        } catch (Exception e) {
            System.out.println("Failed to remove the student with ID: " + studentId);
            System.out.println("Error: " + e.getMessage());
        }

    }

    public List<Student> getAllStudents() {
        return students;
    }

    public void listAllStudents() {
        for (Student student : students) {
            student.displayStudentInfo();
            System.out.println("--------------------");
        }
    }

    public void deactivateStudent(int studentId) {
        try {
            Student student = getStudentById(studentId);
            student.setActive(false);
            System.out.println("Successfully Deactivated the student with ID: " + studentId);
        } catch (Exception e) {
            System.out.println("Failed to Deactivate the student with ID: " + studentId);
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void activateStudent(int studentId){
        try {
            Student student = getStudentById(studentId);
            student.setActive(true);
            System.out.println("Successfully Activated the student with ID: " + studentId);
        } catch (Exception e) {
            System.out.println("Failed to Activate the student with ID: " + studentId);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
