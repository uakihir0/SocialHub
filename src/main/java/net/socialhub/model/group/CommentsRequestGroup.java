package net.socialhub.model.group;

import net.socialhub.service.action.group.CommentsRequestGroupAction;
import net.socialhub.service.action.request.CommentsRequest;

import javax.annotation.Nonnull;
import java.util.List;

public interface CommentsRequestGroup {

    static CommentsRequestGroup of() {
        return new CommentsRequestGroupImpl();
    }

    static CommentsRequestGroup of(CommentsRequest... requests) {
        return new CommentsRequestGroupImpl(requests);
    }

    /**
     * Add Comments Request
     */
    void addCommentsRequests(CommentsRequest request);

    /**
     * Add Comments Requests
     */
    void addCommentsRequests(CommentsRequest... requests);

    /**
     * Get List of Comments Request
     */
    @Nonnull
    List<CommentsRequest> getRequests();

    /**
     * Get Action
     */
    @Nonnull
    CommentsRequestGroupAction action();
}
