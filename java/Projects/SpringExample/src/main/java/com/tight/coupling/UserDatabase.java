package com.tight.coupling;

public class UserDatabase {
    // A - MySql
    // B - Web Service, MongoDB

    // If we change database A to B or any change then we will have to update the userManager
    // Here is Tight Coupling

    public String getUserDetails() {
        return "User Details from Database";
    }

}
