package net.socialhub.service.mastodon.model;

import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.Thread;

public class MastodonThread extends Thread {

    /** 最後のコメント */
    private Comment lastComment;

    public MastodonThread(Service service) {
        super(service);
    }

    // region
    public Comment getLastComment() {
        return lastComment;
    }

    public void setLastComment(Comment lastComment) {
        this.lastComment = lastComment;
    }
    // endregion
}
