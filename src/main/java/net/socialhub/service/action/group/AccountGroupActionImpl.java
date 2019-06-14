package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.AccountGroup;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestAction;
import net.socialhub.utils.HandlingUtil;

import java.util.Date;
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
    public Map<Account, User> getUserMe() {
        return getAccountGroup().getAccounts().parallelStream() //
                .collect(Collectors.toMap(Function.identity(), //
                        (acc) -> acc.action().getUserMe()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getHomeTimeLine() {
        CommentGroupImpl model = new CommentGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();

        Map<Account, Future<Pageable<Comment>>> futures = getAccountGroup() //
                .getAccounts().stream().collect(Collectors.toMap(Function.identity(), //
                        (acc) -> pool.submit(() -> acc.action().getHomeTimeLine(new Paging(200L)))));

        Map<Account, Pageable<Comment>> entities = futures //
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, //
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        Map<Account, RequestAction> actions = getAccountGroup() //
                .getAccounts().stream().collect(Collectors.toMap(Function.identity(), //
                        (acc) -> RequestAction.of(acc.action(), HomeTimeLine)));

        model.setEntities(entities);
        model.setActions(actions);

        model.setSinceDateFromEntities();
        model.setMaxDate(new Date());
        return model;
    }

    //region // Getter&Setter
    public AccountGroup getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(AccountGroup accountGroup) {
        this.accountGroup = accountGroup;
    }
    //endregion
}
