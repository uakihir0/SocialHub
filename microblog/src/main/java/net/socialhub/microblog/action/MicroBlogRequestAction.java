package net.socialhub.microblog.action;

import net.socialhub.core.action.request.CommentsRequest;

public interface MicroBlogRequestAction {

    /**
     * Get Local TimeLine
     */
    CommentsRequest getLocalTimeLine();

    /**
     * Get Federation TimeLine
     */
    CommentsRequest getFederationTimeLine();
}
