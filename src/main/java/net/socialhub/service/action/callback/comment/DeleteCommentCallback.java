package net.socialhub.service.action.callback.comment;

import net.socialhub.model.service.event.IdentifyEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface DeleteCommentCallback extends EventCallback {

    void onDelete(IdentifyEvent event);
}
