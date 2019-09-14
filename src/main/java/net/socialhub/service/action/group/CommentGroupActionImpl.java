package net.socialhub.service.action.group;

import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.utils.HandlingUtil;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Actions for Comment Group
 * コメントグループに対しての操作
 */
public class CommentGroupActionImpl implements CommentGroupAction {

    private CommentGroupImpl commentGroup;

    public CommentGroupActionImpl(CommentGroupImpl commentGroup) {
        this.commentGroup = commentGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getNewComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();

        Map<CommentsRequest, Future<Pageable<Comment>>> futures =
                commentGroup.getEntities().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                (entry) -> pool.submit(() -> {
                                    Paging paging = entry.getValue().newPage();
                                    return entry.getKey().getComments(paging);
                                })));

        Map<CommentsRequest, Pageable<Comment>> entities = futures
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        model.setEntities(entities);
        model.setMaxDateFromEntities();
        model.margeWhenNewPageRequest(commentGroup);
        model.setRequestGroup(commentGroup.getRequestGroup());
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getPastComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        ExecutorService pool = Executors.newCachedThreadPool();

        Map<CommentsRequest, Future<Pageable<Comment>>> futures =
                commentGroup.getEntities().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                (entry) -> pool.submit(() -> {
                                    Paging paging = entry.getValue().pastPage();
                                    return entry.getKey().getComments(paging);
                                })));

        Map<CommentsRequest, Pageable<Comment>> entities = futures
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        (entry) -> HandlingUtil.runtime(() -> entry.getValue().get())));

        model.setEntities(entities);
        model.setSinceDateFromEntities();
        model.margeWhenPastPageRequest(commentGroup);
        model.setRequestGroup(commentGroup.getRequestGroup());
        return model;
    }

    //region // Getter&Setter
    public CommentGroupImpl getCommentGroup() {
        return commentGroup;
    }

    public void setCommentGroup(CommentGroupImpl commentGroup) {
        this.commentGroup = commentGroup;
    }
    //endregion
}
