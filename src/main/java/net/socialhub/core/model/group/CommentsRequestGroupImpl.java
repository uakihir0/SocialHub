package net.socialhub.core.model.group;

import net.socialhub.core.action.group.CommentsRequestGroupAction;
import net.socialhub.core.action.group.CommentsRequestGroupActionImpl;
import net.socialhub.core.action.request.CommentsRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentsRequestGroupImpl implements CommentsRequestGroup {

    /** List of Request Actions */
    private List<CommentsRequest> requests = new ArrayList<>();

    public CommentsRequestGroupImpl(CommentsRequest... requests) {
        addCommentsRequests(requests);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCommentsRequests(CommentsRequest request) {
        if (request != null) {
            this.requests.add(request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCommentsRequests(CommentsRequest... requests) {
        if (requests != null && requests.length > 0) {
            this.requests.addAll(Arrays.asList(requests));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequestGroupAction action() {
        return new CommentsRequestGroupActionImpl(this);
    }

    //region // Getter&Setter
    @Override
    public List<CommentsRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<CommentsRequest> requests) {
        this.requests = requests;
    }
    //endregion
}
