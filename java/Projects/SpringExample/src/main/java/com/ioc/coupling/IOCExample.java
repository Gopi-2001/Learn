package com.ioc.coupling;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IOCExample {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationIoCLooseCouplingExample.xml");

       // UserDataProvider databaseProvider = new UserDatabaseProvider();
       // UserManager userManagerWithDB = new UserManager(databaseProvider);

        UserManager userManagerWithDB = (UserManager) context.getBean("userManagerWithUserDataProvider");
        System.out.println(userManagerWithDB.getUserInfo());

       // UserDataProvider webServiceProvider = new WebServiceDataProvider();
       // UserManager userManagerWithWebService = new UserManager(webServiceProvider);

        UserManager userManagerWithWebService = (UserManager) context.getBean("userManagerWithWebServiceDataProvider");
        System.out.println(userManagerWithWebService.getUserInfo());

        // Note: We have a new provider but we have not touched the implementation of UserManager

        // UserDataProvider newDatabaseProvider = new NewDatabaseProvider();
        // UserManager userManagerWithNewDatabase = new UserManager(newDatabaseProvider);

        UserManager userManagerWithNewDatabase = context.getBean("userManagerWithNewDataProvider", UserManager.class);
        System.out.println(userManagerWithNewDatabase.getUserInfo());
    }

}
