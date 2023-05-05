package net.socialhub.core.action.callback.comment;

import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.service.event.IdentifyEvent;

public interface DeleteCommentCallback extends EventCallback {

    void onDelete(IdentifyEvent event);
}
