package net.socialhub.core.action.callback.comment;

import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.event.NotificationEvent;

public interface NotificationCommentCallback extends EventCallback {

    void onNotification(NotificationEvent reaction);
}
