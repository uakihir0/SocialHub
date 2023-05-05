package net.socialhub.mastodon.model;

import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.Thread;

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
