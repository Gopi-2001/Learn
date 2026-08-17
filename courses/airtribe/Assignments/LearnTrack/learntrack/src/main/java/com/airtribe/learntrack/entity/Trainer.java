package com.airtribe.learntrack.entity;

import com.airtribe.learntrack.common.Person;
import com.airtribe.learntrack.util.IdGenerator;

public class Trainer extends Person {

    public Trainer(String firstName, String lastName,String email) {
        super(IdGenerator.getNextEmployeeId(), firstName, lastName, email);
    }
    
    @Override
    public void getDisplayName() {
        System.out.println("Trainer Name: " + super.getFirstName() + " " + super.getLastName());
    }
}
