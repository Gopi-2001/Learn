package services.impl;

import services.Notification;

public class NotificationFactory {
    public static Notification createNotification() {
        return new EmailNotification();
    }
}