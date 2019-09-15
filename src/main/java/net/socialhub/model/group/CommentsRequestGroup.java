package net.socialhub.model.group;

import net.socialhub.service.action.group.CommentsRequestGroupAction;
import net.socialhub.service.action.request.CommentsRequest;

import java.util.List;

public interface CommentsRequestGroup {

    static CommentsRequestGroup of() {
        return new CommentsRequestGroupImpl();
    }

    static CommentsRequestGroup of(CommentsRequest... requests) {
        return new CommentsRequestGroupImpl(requests);
    }

    /**
     * Add Comments Requests (Array)
     */
    void addCommentsRequests(CommentsRequest... requests);

    /**
     * Get List of Comments Request
     */
    List<CommentsRequest> getRequests();

    /**
     * Get Action
     */
    CommentsRequestGroupAction action();
}
