package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.*;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.utils.HandlingUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;

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
        return new CommentsRequestGroupImpl(requests.toArray(new CommentsRequest[] {})) //
                .action().getComments();
    }
}
