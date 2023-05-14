package net.socialhub.core.action.callback.comment;

import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.event.CommentEvent;

public interface ShareCommentCallback extends EventCallback {

    void onShare(CommentEvent event);
}
