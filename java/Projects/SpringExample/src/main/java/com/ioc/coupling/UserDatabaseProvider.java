package com.ioc.coupling;

public class UserDatabaseProvider implements UserDataProvider {
    // A - MySql
    // B - Web Service, MongoDB

    // Now using Interface we can achieve Loose Coupling
    @Override
    public String getUserDetails()
    {
        return "User Details from Database";
    }

}
