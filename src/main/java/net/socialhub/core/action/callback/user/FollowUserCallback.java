package net.socialhub.core.action.callback.user;

import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.event.UserEvent;

public interface FollowUserCallback extends EventCallback {

    void onFollow(UserEvent event);
}
