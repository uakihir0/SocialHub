package net.socialhub.service.action.callback;

import net.socialhub.model.service.event.UpdateCommentEvent;

public interface UpdateCommentCallback extends EventCallback {

    void onUpdate(UpdateCommentEvent event);
}
