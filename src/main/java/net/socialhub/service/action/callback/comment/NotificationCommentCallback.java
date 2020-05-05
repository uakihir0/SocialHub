package net.socialhub.service.action.callback.comment;

import net.socialhub.model.service.event.NotificationEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface NotificationCommentCallback extends EventCallback {

    void onNotification(NotificationEvent reaction);
}
