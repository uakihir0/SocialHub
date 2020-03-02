package net.socialhub.service.action.callback.comment;

import net.socialhub.model.service.event.DeleteCommentEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface DeleteCommentCallback extends EventCallback {

    void onDelete(DeleteCommentEvent event);
}
