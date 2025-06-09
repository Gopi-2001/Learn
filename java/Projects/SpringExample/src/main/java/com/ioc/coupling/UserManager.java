package com.ioc.coupling;

public class UserManager {
    // Will have a constructor
    // And we will pass implementation to this constructor
    private UserDataProvider userDataProvider;

    public UserManager(UserDataProvider userDataProvider) {
        this.userDataProvider = userDataProvider;
    }

    public String getUserInfo(){
        return userDataProvider.getUserDetails();
    }

}
