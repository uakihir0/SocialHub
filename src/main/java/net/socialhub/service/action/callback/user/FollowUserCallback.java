package net.socialhub.service.action.callback.user;

import net.socialhub.model.service.event.UserEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface FollowUserCallback extends EventCallback {

    void onFollow(UserEvent event);
}
