package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.group.CommentGroupAction;

import java.util.Date;
import java.util.Map;

public interface CommentGroup {

    /**
     * Return Comments related to Accounts
     * アカウントに紐づくコメントを返す
     */
    Map<Account, Pageable<Comment>> getEntities();

    /**
     * Return Actions related to Accounts
     * アカウントに紐づくクエリアクションを返す
     */
    Map<Account, RequestAction> getActions();

    /**
     * Return Order Decided Comments
     * (Return Comments by Created Date DESC)
     * 順序が決定している部分までコメント情報を返す
     */
    Pageable<Comment> getComments();

    Date getMaxDate();

    Date getSinceDate();

    CommentGroupAction action();
}
