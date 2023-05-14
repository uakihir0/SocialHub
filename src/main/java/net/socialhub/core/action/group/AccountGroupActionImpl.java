package net.socialhub.core.action.group;

import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.User;
import net.socialhub.core.model.group.AccountGroup;
import net.socialhub.core.model.group.CommentGroup;
import net.socialhub.core.model.group.CommentsRequestGroupImpl;
import net.socialhub.core.model.group.UserGroup;
import net.socialhub.core.model.group.UserGroupImpl;
import net.socialhub.core.utils.HandlingUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * グループアクション
 */
public class AccountGroupActionImpl implements AccountGroupAction {

    private AccountGroup accountGroup;

    public AccountGroupActionImpl(AccountGroup accountGroup) {
        this.accountGroup = accountGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroup getUserMe() {
        UserGroupImpl model = new UserGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();

        Map<Account, Future<User>> futures = accountGroup //
                .getAccounts().stream().collect(Collectors.toMap(Function.identity(), //
                        (acc) -> pool.submit(() -> acc.action().getUserMe())));

        Map<Account, User> entities = futures //
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, //
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        model.setEntities(entities);
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getHomeTimeLine() {
        List<CommentsRequest> requests = accountGroup.getAccounts().stream() //
                .map((acc) -> acc.action().request().getHomeTimeLine()) //
                .collect(Collectors.toList());
        return getComments(requests);
    }

    private CommentGroup getComments(List<CommentsRequest> requests) {
        return new CommentsRequestGroupImpl(requests.toArray(new CommentsRequest[]{})) //
                .action().getComments();
    }
}
