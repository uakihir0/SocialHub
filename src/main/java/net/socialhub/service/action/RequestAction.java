package net.socialhub.service.action;

import net.socialhub.model.Account;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

import static net.socialhub.define.ActionType.*;

public interface RequestAction {

    /**
     * Get Queried TimeLine
     * リクエストされたタイムラインを取得
     */
    default Pageable<Comment> getTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    static RequestAction homeTimeLine(Account account) {
        return new RequestActionImpl(account.action(), HomeTimeLine);
    }

    static RequestAction mentionTimeLine(Account account) {
        return new RequestActionImpl(account.action(), MentionTimeLine);
    }

    static RequestAction userCommentTimeLine(Account account, Identify id) {
        return new RequestActionImpl(account.action(), UserCommentTimeLine, id);
    }

    static RequestAction userLikeTimeLine(Account account, Identify id) {
        return new RequestActionImpl(account.action(), UserLikeTimeLine, id);
    }
}
