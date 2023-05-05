package net.socialhub.core.model.service.event;

import net.socialhub.core.model.service.Notification;

public class NotificationEvent {

    private Notification notification;

    public NotificationEvent(Notification notification) {
        this.notification = notification;
    }

    // region
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    // endregion
}
