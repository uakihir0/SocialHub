package net.socialhub.model.group;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.service.action.group.CommentGroupAction;
import net.socialhub.service.action.request.CommentsRequest;

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
     * Get Actions
     */
    CommentGroupAction action();
}
