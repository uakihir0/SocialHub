package net.socialhub.service.action.callback.comment;

import net.socialhub.model.service.event.UpdateCommentEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface UpdateCommentCallback extends EventCallback {

    void onUpdate(UpdateCommentEvent event);
}
