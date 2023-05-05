package net.socialhub.core.model.group;

import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.action.group.CommentGroupAction;
import net.socialhub.core.action.request.CommentsRequest;

import java.util.Date;
import java.util.Map;

public interface CommentGroup {

    /**
     * Get Comments Request Groups
     * コメントグループの取得
     */
    CommentsRequestGroup getRequestGroup();

    /**
     * Return Comments related to Comments Requests
     * コメントリクエスト毎に紐づくコメントを取得
     */
    Map<CommentsRequest, Pageable<Comment>> getEntities();

    /**
     * Return Order Decided Comments
     * (Return Comments by Created Date DESC)
     * 順序が決定している部分までコメントを取得
     */
    Pageable<Comment> getComments();

    /**
     * Get MaxDate for Paging Request
     */
    Date getMaxDate();

    /**
     * Get SinceDate for Paging Request
     */
    Date getSinceDate();

    /**
     * Set Newest comment. (for streaming)
     */
    void setNewestComment(Comment comment);

    /**
     * Set Oldest comment.
     */
    void setOldestComment(Comment comment);


    /**
     * Get Actions
     */
    CommentGroupAction action();
}
