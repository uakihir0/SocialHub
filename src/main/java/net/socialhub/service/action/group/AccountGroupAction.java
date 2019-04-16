package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.service.User;

import java.util.Map;

public interface AccountGroupAction {

    /**
     * Get All User's Information
     * グループのユーザー情報を取得
     */
    default Map<Account, User> getUserMe() {
        throw new NotImplimentedException();
    }

    /**
     * Get Timeline Comments
     * タイムラインを取得
     */
    default CommentGroup getHomeTimeLine() {
        throw new NotImplimentedException();
    }
}
