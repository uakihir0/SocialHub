package net.socialhub.core.model.group;

import net.socialhub.core.action.group.UsersRequestGroupAction;
import net.socialhub.core.action.request.UsersRequest;

import javax.annotation.Nonnull;
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
    @Nonnull
    List<UsersRequest> getRequests();

    /**
     * Get Action
     */
    @Nonnull
    UsersRequestGroupAction action();
}
