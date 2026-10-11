package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

public final class Validator {
    private Validator() { }
    
    public static void requireName(String name) throws InvalidDataException { 
        if (name == null || name.trim().isEmpty()) throw new InvalidDataException("Name is required"); 
    }
    
    public static void requireAge(int age) throws InvalidDataException { 
        if (age < 0 || age > 130) throw new InvalidDataException("Age must be 0 to 130"); 
    }
    
    public static void requirePhone(String phone) throws InvalidDataException { 
        if (phone == null || phone.trim().isEmpty()) throw new InvalidDataException("Phone is required"); 
    }
}
