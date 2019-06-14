package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.group.RequestGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.utils.HandlingUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class RequestGroupActionImpl implements RequestGroupAction {

    private RequestGroupImpl requestGroup;

    public RequestGroupActionImpl(RequestGroupImpl requestGroup) {
        this.requestGroup = requestGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();

        Map<Account, Future<Pageable<Comment>>> futures = requestGroup //
                .getActions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, //
                        (entry) -> pool.submit(() -> entry.getValue().getComments(new Paging(200L)))));

        Map<Account, Pageable<Comment>> entities = futures //
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, //
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        model.setEntities(entities);
        model.setSinceDateFromEntities();
        model.setActions(new HashMap<>(requestGroup.getActions()));
        return model;
    }
}
