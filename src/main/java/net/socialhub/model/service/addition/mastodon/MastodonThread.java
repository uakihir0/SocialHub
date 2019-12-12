package net.socialhub.model.service.addition.mastodon;

import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;

public class MastodonThread extends Thread {

    /** 最後のコメントの ID */
    private Long lastCommentId;

    public MastodonThread(Service service) {
        super(service);
    }

    // region
    public Long getLastCommentId() {
        return lastCommentId;
    }

    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }
    // endregion
}
