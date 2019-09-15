package net.socialhub.model.group;

import net.socialhub.service.action.group.UsersRequestGroupAction;
import net.socialhub.service.action.request.UsersRequest;

import java.util.List;

public interface UsersRequestGroup {

    static UsersRequestGroup of() {
        return new UsersRequestGroupImpl();
    }

    static UsersRequestGroup of(UsersRequest... requests) {
        return new UsersRequestGroupImpl(requests);
    }

    /**
     * Add Users Request
     */
    void addUsersRequests(UsersRequest request);

    /**
     * Add Users Requests
     */
    void addUsersRequests(UsersRequest... requests);

    /**
     * Get List of Users Request
     */
    List<UsersRequest> getRequests();


    /**
     * Get Action
     */
    UsersRequestGroupAction action();
}
