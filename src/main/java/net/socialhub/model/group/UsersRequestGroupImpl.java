package net.socialhub.model.group;

import net.socialhub.service.action.group.UsersRequestGroupAction;
import net.socialhub.service.action.group.UsersRequestGroupActionImpl;
import net.socialhub.service.action.request.UsersRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersRequestGroupImpl implements UsersRequestGroup {

    /** List of Request Actions */
    private List<UsersRequest> requests = new ArrayList<>();

    public UsersRequestGroupImpl(UsersRequest... requests) {
        addUsersRequests(requests);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUsersRequests(UsersRequest... requests) {
        if (requests != null && requests.length > 0) {
            this.requests.addAll(Arrays.asList(requests));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequestGroupAction action() {
        return new UsersRequestGroupActionImpl(this);
    }

    //region // Getter&Setter
    @Override
    public List<UsersRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<UsersRequest> requests) {
        this.requests = requests;
    }
    //endregion
}
