package net.socialhub.service.action.callback.comment;

import net.socialhub.model.service.event.CommentEvent;
import net.socialhub.service.action.callback.EventCallback;

public interface ShareCommentCallback extends EventCallback {

    void onShare(CommentEvent event);
}
