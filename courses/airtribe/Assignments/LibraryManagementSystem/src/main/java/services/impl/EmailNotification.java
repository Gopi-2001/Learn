package services.impl;

import services.Notification;

public class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("[EMAIL ALERT] " + message);
    }
}