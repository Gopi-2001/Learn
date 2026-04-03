package com.tight.coupling;

public class TightCouplingExample {
    // Entry Point for our application

    public static void main(String[] args) {
        UserManager userManager = new UserManager();

        System.out.println(userManager.getUserInfo());
    }

}
