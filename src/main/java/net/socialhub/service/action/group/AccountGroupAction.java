package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.service.User;

import java.util.Map;

public interface AccountGroupAction {

    /**
     * Get All User's Information
     * グループのユーザー情報を取得
     */
    Map<Account, User> getUserMe();

    /**
     * Get Timeline Comments
     * タイムラインを取得
     */
    CommentGroup getHomeTimeLine();
}
