package net.socialhub.service.action.specific;

import net.socialhub.service.action.request.CommentsRequest;

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
