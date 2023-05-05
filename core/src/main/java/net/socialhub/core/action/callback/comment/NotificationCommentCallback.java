package net.socialhub.core.action.callback.comment;

import net.socialhub.core.model.service.event.NotificationEvent;
import net.socialhub.core.action.callback.EventCallback;

public interface NotificationCommentCallback extends EventCallback {

    void onNotification(NotificationEvent reaction);
}
