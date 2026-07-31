package com.airtribe.learntrack.entity;

import com.airtribe.learntrack.common.Person;
import com.airtribe.learntrack.util.IdGenerator;

public class Student extends Person {

    private String batch;
    private boolean active;

    public Student(String firstName, String lastName, String batch, boolean active) {
        super(IdGenerator.getNextStudentId(), firstName, lastName, "");
        this.batch = batch;
        this.active = active;
    }

    public Student(String firstName, String lastName, String email, String batch, boolean active) {
        this(firstName, lastName, batch, active);
        super.setEmail(email);
    }

    @Override
    public void getDisplayName() {
        System.out.println("Student Name: " + super.getFirstName() + " " + super.getLastName());
    }

    public void displayStudentInfo() {
        System.out.println("Student ID: " + super.getId());
        System.out.println("Name: " + super.getFirstName() + " " + super.getLastName());
        System.out.println("Email: " + super.getEmail());
        System.out.println("Batch: " + batch);
        System.out.println("Active: " + active);
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
