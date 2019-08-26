package net.socialhub.service.action.callback;

import net.socialhub.model.service.event.DeleteCommentEvent;

public interface DeleteCommentCallback extends EventCallback {

    void onDelete(DeleteCommentEvent event);
}
