package net.socialhub.service.action.group;

import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.group.CommentsRequestGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.utils.HandlingUtil;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommentsRequestGroupActionImpl implements CommentsRequestGroupAction {

    private CommentsRequestGroupImpl requestGroup;

    public CommentsRequestGroupActionImpl(CommentsRequestGroupImpl requestGroup) {
        this.requestGroup = requestGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getComments() {
        return getComments(new Paging(200L));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getComments(int count) {
        return getComments(new Paging((long) count));
    }

    /**
     * コメント情報をページング付きで取得
     */
    private CommentGroup getComments(Paging paging) {
        CommentGroupImpl model = new CommentGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();
        Paging copiedPage = (paging != null) ? paging.copy() : null;

        Map<CommentsRequest, Future<Pageable<Comment>>> futures = requestGroup //
                .getRequests().stream().collect(Collectors.toMap(Function.identity(), //
                        (request) -> pool.submit(() -> request.getComments(copiedPage))));

        Map<CommentsRequest, Pageable<Comment>> entities = futures //
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, //
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        model.setEntities(entities);
        model.setMaxDate(new Date());
        model.setSinceDateFromEntities();
        model.setRequestGroup(requestGroup);
        return model;
    }
}
