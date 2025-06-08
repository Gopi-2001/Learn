package com.loose.coupling;

public class LooseCouplingExample {

    public static void main(String[] args) {
        UserDataProvider databaseProvider = new UserDatabaseProvider();
        UserManager userManagerWithDB = new UserManager(databaseProvider);

        System.out.println(userManagerWithDB.getUserInfo());

        UserDataProvider webServiceProvider = new WebServiceDataProvider();
        UserManager userManagerWithWebService = new UserManager(webServiceProvider);

        System.out.println(userManagerWithWebService.getUserInfo());

        // Note: We have a new provider but we have not touched the implementation of UserManager

        UserDataProvider newDatabaseProvider = new NewDatabaseProvider();
        UserManager userManagerWithNewDatabase = new UserManager(newDatabaseProvider);
        System.out.println(userManagerWithNewDatabase.getUserInfo());
    }

}
