package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;

public abstract class Person extends MedicalEntity implements Cloneable {
    private String name;
    private int age;
    private String phone;

    protected Person(String id, String name, int age, String phone) throws InvalidDataException {
        super(id);
        setName(name);
        setAge(age);
        setPhone(phone);
    }

    public String getName() { 
        return name; 
    }

    public int getAge() { 
        return age; 
    }

    public String getPhone() { 
        return phone; 
    }
    
    public void setName(String name) throws InvalidDataException { 
        Validator.requireName(name); 
        this.name = name; 
    }

    public void setAge(int age) throws InvalidDataException { 
        Validator.requireAge(age); 
        this.age = age; 
    }

    public void setPhone(String phone) throws InvalidDataException { 
        Validator.requirePhone(phone); 
        this.phone = phone; 
    }

    @Override 
    public String getDisplayName() { 
        return name + " (" + getId() + ")"; 
    }

    @Override
    protected Person clone() throws CloneNotSupportedException {
        return (Person) super.clone();
    }
}
